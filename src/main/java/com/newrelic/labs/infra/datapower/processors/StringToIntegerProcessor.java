package com.newrelic.labs.infra.datapower.processors;

public class StringToIntegerProcessor implements MetricProcessor {

	@Override
	public Number process(String metricPath, String value) throws MetricProcessingException {
		try {
			Integer i = Integer.parseInt(value);
			return i;
		} catch (Exception e) {
			throw new MetricProcessingException(e);
		}
	}

}
