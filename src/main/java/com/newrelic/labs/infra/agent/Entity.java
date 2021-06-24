package com.newrelic.labs.infra.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Entity<T extends Arguments> {
	
	private String name;
	private BlockingQueue<MetricSet> blockingMetricSetQueue = new LinkedBlockingQueue<>();
	private Integration<T> integration;

	protected Entity(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public MetricSet newMetricSet(String metricSetName) {
		MetricSet m = new MetricSet();
		blockingMetricSetQueue.add(m);
		m.metricSetName = metricSetName;
		return m;
	}

	public void publish() {		
		Collection<MetricSet> metricSets = new ArrayList<MetricSet>();
		int n = blockingMetricSetQueue.drainTo(metricSets);
		if (n > 0) {
			integration.publish(this.getName(), this.getEntityType(), metricSets);
		}
	}
	
	public abstract String getEntityType();
	
	public abstract void poll() throws Exception;

	public abstract void cleanup();

	protected void setIntegration(Integration<T> integration) {
		this.integration = integration;
	}

	protected abstract void validate(T parsedArgs) throws Exception;
	
	protected abstract void init(T parsedArgs) throws Exception;
	
	protected abstract T populateBean(Map<String, Object> instanceProperties) throws Exception;

}
