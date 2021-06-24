package com.newrelic.labs.infra.datapower.processors;

public interface MetricProcessor {
	public Number process(String metricPath, String value) throws MetricProcessingException;
}
