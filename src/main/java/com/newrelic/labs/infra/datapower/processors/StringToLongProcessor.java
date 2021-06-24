package com.newrelic.labs.infra.datapower.processors;

public class StringToLongProcessor implements MetricProcessor {

	@Override
	public Number process(String metricPath, String value) throws MetricProcessingException {
		try {
			Long i = Long.parseLong(value);
			return i;
		} catch (Exception e) {
			throw new MetricProcessingException(e);
		}
	}

}
