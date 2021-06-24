package com.newrelic.labs.infra.output.infra;

import java.util.List;
import java.util.Map;

public class EntityItem {

	private EntityData entity;
	private List<Map<String, Object>> metrics;
	
	public EntityData getEntity() {
		return entity;
	}
	
	public void setEntity(EntityData entity) {
		this.entity = entity;
	}

	public List<Map<String, Object>> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<Map<String, Object>> metrics) {
		this.metrics = metrics;
	}


}
