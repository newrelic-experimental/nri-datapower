package com.newrelic.labs.infra.datapower.processors;

public class StringToFloatProcessor implements MetricProcessor {

	@Override
	public Number process(String metricPath, String value) throws MetricProcessingException {
		try {
			Float i = Float.parseFloat(value);
			return i;
		} catch (Exception e) {
			throw new MetricProcessingException(e);
		}
	}

}
