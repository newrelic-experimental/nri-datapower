package com.newrelic.labs.infra.datapower;

public class DPClassDescriptor {

	private DPMetricDescriptor[] metrics = null;
	private String metricPath = null;
	private String metricClampComparatorAttribute = null;
	
	private String eventType = null;
	private String[] stringAttributes = null;
	private String[] intAttributes = null;
	private String[] longAttributes = null;
	private String[] longAttributesDelta = null;
	private String[] floatAttributes = null;
	
	public DPMetricDescriptor[] getMetrics() {
		return metrics;
	}
	public void setMetrics(DPMetricDescriptor[] metricDefArray) {
		this.metrics = metricDefArray;
	}
	public String getMetricPath() {
		return metricPath;
	}
	public void setMetricPath(String metricPath) {
		this.metricPath = metricPath;
	}
	public String getMetricClampComparatorAttribute() {
		return metricClampComparatorAttribute;
	}
	public void setMetricClampComparatorAttribute(String metricClampComparatorAttribute) {
		this.metricClampComparatorAttribute = metricClampComparatorAttribute;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String[] getIntAttributes() {
		return intAttributes;
	}
	public void setIntAttributes(String[] intAttributes) {
		this.intAttributes = intAttributes;
	}
	public String[] getLongAttributes() {
		return longAttributes;
	}
	public void setLongAttributes(String[] longAttributes) {
		this.longAttributes = longAttributes;
	}
	public String[] getLongAttributesDelta() {
		return longAttributesDelta;
	}
	public void setLongAttributesDelta(String[] longAttributesDelta) {
		this.longAttributesDelta = longAttributesDelta;
	}
	public String[] getFloatAttributes() {
		return floatAttributes;
	}
	public void setFloatAttributes(String[] floatAttributes) {
		this.floatAttributes = floatAttributes;
	}
	public String[] getStringAttributes() {
		return stringAttributes;
	}
	public void setStringAttributes(String[] stringAttributes) {
		this.stringAttributes = stringAttributes;
	}
	
}
