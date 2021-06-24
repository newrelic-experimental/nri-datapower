package com.newrelic.labs.infra.agent;

import org.apache.commons.beanutils.BeanUtils;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Arguments {
	@Parameter(names = "--help", description = "This usage help", help = true)
	private boolean help;

	public boolean isHelp() {
		return help;
	}

	@Parameter(names = "-config_file", description = "Path to config file")
	private String configFile = null;

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	@Parameter(names = "-mode", description = "Mode of operation, either `insights` or `infra`")
	private String mode = "insights";

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
	
	@Parameter(names = "--verbose", description = "Verbose logging")
	private boolean verbose = false;

	public boolean getVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void setupArgs(String[] args) {
		JCommander jct = JCommander.newBuilder().addObject(this).build();
		jct.parse(args);
		if (isHelp()) {
			jct.usage();
		}
		try {
			BeanUtils.populate(this, System.getenv());
		} catch (Exception e) {
			System.err.println("Error populating arguments from system environment");
		} 
	}
}