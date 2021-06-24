package com.newrelic.labs.infra.datapower;

import com.newrelic.labs.infra.datapower.processors.MetricProcessor;

public class DPMetricDescriptor {
	private String name = null;
	private MetricProcessor processor = null;
	private String unit = "";
    private String metricClampComparatorAttribute = null;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public MetricProcessor getProcessor() {
		return processor;
	}
	public void setProcessor(MetricProcessor processor) {
		this.processor = processor;
	}
	public String getMetricClampComparatorAttribute() {
		return metricClampComparatorAttribute;
	}
	public void setMetricClampComparatorAttribute(String metricClampComparatorAttribute) {
		this.metricClampComparatorAttribute = metricClampComparatorAttribute;
	}
}
