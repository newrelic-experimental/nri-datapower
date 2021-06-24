package com.newrelic.labs.infra.agent;

import java.util.HashMap;
import java.util.Map;

public class MetricSet {

	protected String metricSetName = null;
	protected Map<String, String> attributes;
	protected Map<String, Number> metrics;

	protected MetricSet() {
		attributes = new HashMap<String, String>();
		metrics = new HashMap<String, Number>();
	}

	public void setMetric(String key, Number value, SourceType sourceType) {
		switch (sourceType) {
		case GAUGE:
			metrics.put(key, value);
			break;
		case RATE:
			metrics.put(key, value);
			break;
		case DELTA:
			metrics.put(key, value);
			break;
		case ATTRIBUTE:
			attributes.put(key, value.toString());
			break;
		default:
		}
	}

	public String getMetricSetName() {
		return metricSetName;
	}

	public void setMetricSetName(String metricSetName) {
		this.metricSetName = metricSetName;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttribute(String key, String value) {
		this.attributes.put(key, value);
	}

	public Map<String, Number> getMetrics() {
		return metrics;
	}

	public void setMetrics(Map<String, Number> metrics) {
		this.metrics = metrics;
	}

	
}
