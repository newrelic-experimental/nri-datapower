package com.newrelic.labs.infra.agent;

public class Metric {
	
	public String key;
	public String value;
	public SourceType type;
	
	protected Metric(String key, String value, SourceType type) {
		super();
		this.key = key;
		this.value = value;
		this.type = type;
	}

}
