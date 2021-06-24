package com.newrelic.labs.infra.datapower;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.newrelic.labs.infra.agent.Entity;
import com.newrelic.labs.infra.agent.Integration;
import com.newrelic.labs.infra.datapower.config.DPCollect;

public class DPIntegration extends Integration<DPArgs> {
	
	private static Logger logger = LoggerFactory.getLogger(DPIntegration.class);
	private DPCollect systemMetricDefs;
	private DPCollect domainMetricDefs;
	
	public DPIntegration(String integrationName, String integrationVersion) {
		super(integrationName, integrationVersion);
	}

	@Override
	public void init() throws Exception {
		String systemMetricsYmlFile = "/system-metrics.yml";
		DPCollect systemMetricDefs = loadMetricsYaml(systemMetricsYmlFile);
		this.systemMetricDefs = systemMetricDefs;
		
		String domainMetricsYmlFile = "/domain-metrics.yml";
		DPCollect domainMetricDefs = loadMetricsYaml(domainMetricsYmlFile);
		this.domainMetricDefs = domainMetricDefs;
	}
	
	@Override
	public Entity<DPArgs> newEntity(String name) throws Exception {
		return new DPEntity<DPArgs>(name, systemMetricDefs, domainMetricDefs);
	}
	
	private DPCollect loadMetricsYaml(String systemMetricsYmlFile) throws Exception {
		try {
			Yaml yaml = new Yaml(new Constructor(DPCollect.class));
			InputStream in = getClass().getResourceAsStream(systemMetricsYmlFile); 
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			DPCollect SystemMetricDefs = yaml.load(reader);
			return SystemMetricDefs;
		} catch (Exception e) {
			System.err.println("Unable to parse configuration from " + systemMetricsYmlFile);
			e.printStackTrace();
			throw e;
		}
	}

}