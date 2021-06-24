package com.newrelic.labs.infra.output.insights;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

public class SingleThreadedInsightsClient extends InsightsClient {

	public SingleThreadedInsightsClient(String insightsAPIUrl, String insightsAPIInsertKey) {
		super(insightsAPIUrl, insightsAPIInsertKey);
	}
	
	@Override
	public void init(ClientConnectionConfiguration connConfig) {
		RequestConfig requestConfig; 
		List<Header> headers = new ArrayList<Header>();
		
		//Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, PROPERTY_VALUE_HEADER_CONTENT_TYPE);
		Header insertKeyHeader = new BasicHeader(PROPERTY_NAME_HEADER_X_INSERT_KEY, insightsAPIInsertKey);
		headers.add(insertKeyHeader);
		
		if (connConfig.isUseProxy()) {
			HttpHost proxy = new HttpHost(connConfig.getProxyHost(), connConfig.getProxyPort(), connConfig.getProxyScheme());
			requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connConfig.getConnectionRequestTimeout())
				.setConnectTimeout(connConfig.getConnectTimeout()).setSocketTimeout(connConfig.getSocketTimeout())
				.setProxy(proxy )
				.build();
			if ((connConfig.getProxyUsername() != null) && (connConfig.getProxyPassword() != null)) {
				CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
				credentialsProvider.setCredentials(
			            new AuthScope(connConfig.getProxyHost(), connConfig.getProxyPort()),
			            new UsernamePasswordCredentials((connConfig.getProxyUsername()).trim(), connConfig.getProxyPassword().trim()));
				httpclient  = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setDefaultHeaders(headers).setDefaultCredentialsProvider(credentialsProvider).build();
			} else {
				httpclient  = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setDefaultHeaders(headers).build();
			}
		} else {
			requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connConfig.getConnectionRequestTimeout())
				.setConnectTimeout(connConfig.getConnectTimeout()).setSocketTimeout(connConfig.getSocketTimeout())
				.build();
			httpclient  = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setDefaultHeaders(headers).build();
		}	
	}
}
