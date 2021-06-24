package com.newrelic.labs.infra.agent;

import java.util.List;

public class EntityConnectionInfo {

	private String integration_name;
	
	private List<EntityInstanceInfo> instances;

	public String getIntegration_name() {
		return integration_name;
	}

	public void setIntegration_name(String integration_name) {
		this.integration_name = integration_name;
	}

	public List<EntityInstanceInfo> getInstances() {
		return instances;
	}

	public void setInstances(List<EntityInstanceInfo> instances) {
		this.instances = instances;
	}
	
}
