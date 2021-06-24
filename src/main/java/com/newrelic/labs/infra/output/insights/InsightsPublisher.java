package com.newrelic.labs.infra.output.insights;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newrelic.labs.infra.agent.MetricSet;
import com.newrelic.labs.infra.agent.Publisher;

public class InsightsPublisher extends Publisher {
	private static Logger logger = LoggerFactory.getLogger(InsightsPublisher.class);
	
	private static final String EVENT_TYPE = "eventType";
	private static ObjectMapper mapper = new ObjectMapper();
	private InsightsClient client;

	private boolean verbose = false;

	public InsightsPublisher(InsightsClient insightsClient, boolean verbose) {
		this.client = insightsClient;
		this.verbose  = verbose;
	}

	@Override
	public void publish(String entityName, String entityType, Collection<MetricSet> metricSets) {
		List<Map<String, Object>> payloadData = new ArrayList<Map<String, Object>>(metricSets.size());

		for (Iterator<MetricSet> iterator = metricSets.iterator(); iterator.hasNext();) {
			MetricSet metricSet = iterator.next();
			Map<String, Object> event = new HashMap<String, Object>();
			payloadData.add(event);
			event.put(EVENT_TYPE, metricSet.getMetricSetName());
			for (Iterator<String> msIter = metricSet.getMetrics().keySet().iterator(); msIter.hasNext();) {
				String metricKey = msIter.next();
				Number metricValue = metricSet.getMetrics().get(metricKey);
				event.put(metricKey, metricValue);
			}
			for (Iterator<String> attrIter = metricSet.getAttributes().keySet().iterator(); attrIter.hasNext();) {
				String attrKey = attrIter.next();
				event.put(attrKey, metricSet.getAttributes().get(attrKey));
			}
		}

		try {
			byte[] payload = mapper.writeValueAsBytes(payloadData);
			if (verbose) {
				logger.info("Posting with metric payload: " + new String(payload));
			}
			client.post(payload);
		} catch (JsonProcessingException e) {
			System.err.println("Unable to create insights HTTP POST data");
			e.printStackTrace();
		} catch (Throwable t) {
			System.err.println("Unable to post metrics to New Relic Insights collector");
			t.printStackTrace();
		}
	}

	@Override
	public void shutdown() {
		client.destroyClient();
	}

}
