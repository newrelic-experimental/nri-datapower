package com.newrelic.labs.infra.output.infra;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Payload {

	private String name;
	
	@JsonProperty("protocol_version")
	private String protocolVersion;
	
	@JsonProperty("integration_version")
	private String integrationVersion;
	
	private List<EntityItem> data;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProtocolVersion() {
		return protocolVersion;
	}
	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}
	public String getIntegrationVersion() {
		return integrationVersion;
	}
	public void setIntegrationVersion(String integrationVersion) {
		this.integrationVersion = integrationVersion;
	}
	public List<EntityItem> getData() {
		return data;
	}
	public void setData(List<EntityItem> data) {
		this.data = data;
	}
	
	
}