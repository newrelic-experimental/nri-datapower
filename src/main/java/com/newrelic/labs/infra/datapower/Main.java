package com.newrelic.labs.infra.datapower;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newrelic.labs.infra.agent.Integration;

public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		try {
			Integration<DPArgs> integration = new DPIntegration("com.newrelic.labs.nri.datapower", "0.9");
			DPArgs arguments = new DPArgs();
			arguments.setupArgs(args);
			integration.run(arguments);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}

	}
	
}