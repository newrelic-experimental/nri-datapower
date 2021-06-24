package com.newrelic.labs.infra.agent;

public class InsightsConnectionInfo {

	private String insights_insert_key = null;
	private String collector_url = null;
	private String proxy_host = null;
	private Integer proxy_port = 5443;
	private String proxy_user = null;
	private String proxy_password = null;

	public String getInsights_insert_key() {
		return insights_insert_key;
	}

	public void setInsights_insert_key(String insights_insert_key) {
		this.insights_insert_key = insights_insert_key;
	}

	public String getCollector_url() {
		return collector_url;
	}

	public void setCollector_url(String collector_url) {
		this.collector_url = collector_url;
	}

	public String getProxy_host() {
		return proxy_host;
	}

	public void setProxy_host(String proxy_host) {
		this.proxy_host = proxy_host;
	}

	public Integer getProxy_port() {
		return proxy_port;
	}

	public void setProxy_port(Integer proxy_port) {
		this.proxy_port = proxy_port;
	}

	public String getProxy_user() {
		return proxy_user;
	}

	public void setProxy_user(String proxy_user) {
		this.proxy_user = proxy_user;
	}

	public String getProxy_password() {
		return proxy_password;
	}

	public void setProxy_password(String proxy_password) {
		this.proxy_password = proxy_password;
	}

	
}
