package com.newrelic.labs.infra.datapower;

import com.beust.jcommander.Parameter;
import com.newrelic.labs.infra.agent.Arguments;

public class DPArgs extends Arguments {

	@Parameter(names = "-host", description = "Host name of the datapower device")
	private String host = "127.0.0.1";
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Parameter(names = "-port", description = "Port number of datapower XML Management interface")
	private Integer port = 5550;

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
	
	@Parameter(names = "-username", description = "Username")
	private String username = "admin";

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Parameter(names = "-password", description = "Password", password = true)
	private String password = "admin";

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Parameter(names = "-domains", description = "comma separated list of domains", listConverter = DomainListConverter.class)
	private String domains = null;

	public String getDomains() {
		return domains;
	}

	public void setDomains(String domains) {
		this.domains = domains;
	}

}
