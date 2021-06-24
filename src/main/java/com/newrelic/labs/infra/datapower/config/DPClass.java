package com.newrelic.labs.infra.datapower.config;

import java.util.List;

public class DPClass {

	private String metric_class;
	private String event_type;
	private List<DPMetric> metrics;

	public String getMetric_class() {
		return metric_class;
	}

	public void setMetric_class(String metric_class) {
		this.metric_class = metric_class;
	}

	public String getEvent_type() {
		return event_type;
	}

	public void setEvent_type(String event_type) {
		this.event_type = event_type;
	}

	public List<DPMetric> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<DPMetric> metrics) {
		this.metrics = metrics;
	}

}
