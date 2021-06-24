package com.newrelic.labs.infra.datapower.processors;

public class OkStateProcessor implements MetricProcessor {

	Integer up = new Integer(1);
	Integer down = new Integer(0);
	
	@Override
	public Number process(String metricPath, String opState) throws MetricProcessingException {
		if (opState.equalsIgnoreCase("ok")) {
			return up;
		} else {
			return down;
		}
	}
		
}
