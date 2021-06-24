package com.newrelic.labs.infra.output.insights;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class InsightsClient {

	protected static final String PROPERTY_NAME_HEADER_X_INSERT_KEY = "X-Insert-Key";
	protected CloseableHttpClient httpclient = null;
	protected String insightsAPIUrl = null;
	protected String insightsAPIInsertKey = null;

	// This should be threadsafe but I will doublecheck later
	ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public InsightsClient(String insightsAPIUrl, String insightsAPIInsertKey) {
		this.insightsAPIUrl = insightsAPIUrl;
		this.insightsAPIInsertKey = insightsAPIInsertKey;
	}

	public abstract void init(ClientConnectionConfiguration httpConfig);

	public void init() {
		this.init(new ClientConnectionConfiguration());
	}

	public boolean post(byte[] payload) {
		boolean result = false;
		HttpPost postrequest = new HttpPost(insightsAPIUrl);
		try {
			ByteArrayOutputStream jsonBytesCompressed = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(jsonBytesCompressed);
			gzip.write(payload);
			gzip.close();

			EntityBuilder entityBuilder = EntityBuilder.create();
			entityBuilder.setBinary(jsonBytesCompressed.toByteArray());
			entityBuilder.setContentType(ContentType.APPLICATION_JSON);
			entityBuilder.setContentEncoding("gzip");

			postrequest.setEntity(entityBuilder.build());

			CloseableHttpResponse response = httpclient.execute(postrequest);
			int status = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			//System.err.println("Response is " + response.getStatusLine());
			if (status >= 200 && status < 300) {
				try {
					if (entity != null) {
						InputStream input = entity.getContent();
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
						InsightsResponse insightsStatus = mapper.readValue(bufferedReader, InsightsResponse.class);
						if (insightsStatus.isSuccess()) {
							result = true;
						}
					}
				} finally {
					EntityUtils.consumeQuietly(entity);
					response.close();
				}
			} else {
				String responseBody = EntityUtils.toString(response.getEntity());
				System.err.println("Response error is " + responseBody);
				EntityUtils.consumeQuietly(entity);
				response.close();
			}

		} catch (Exception e) {
			System.err.println("Unable to post payload to New Relic Collector. " + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	public void destroyClient() {
		try {
			httpclient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
