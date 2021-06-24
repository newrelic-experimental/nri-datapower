package com.newrelic.labs.infra.datapower.config;

public class DPMetric {

	private String metric_name;
	private String metric_type = "auto";

	public String getMetric_name() {
		return metric_name;
	}

	public void setMetric_name(String metric_name) {
		this.metric_name = metric_name;
	}

	public String getMetric_type() {
		return metric_type;
	}

	public void setMetric_type(String metric_type) {
		this.metric_type = metric_type;
	}

}
