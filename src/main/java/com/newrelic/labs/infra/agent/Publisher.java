package com.newrelic.labs.infra.agent;

import java.util.Collection;

public abstract class Publisher {
	
	public abstract void publish(String entityName, String entityType, Collection<MetricSet> metricSets);

	public abstract void shutdown();

}
