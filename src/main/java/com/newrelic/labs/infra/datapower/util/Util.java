package com.newrelic.labs.infra.datapower.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.newrelic.labs.infra.datapower.DPClassDescriptor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Util {
	
	private static Map<String, Map<String, DPClassDescriptor>> metricSetConfigurationTable = new HashMap<String, Map<String, DPClassDescriptor>>();
	
	public static void addMetricSet(File metricTypesFile) {
		
		System.out.println("Parsing file " + metricTypesFile.getAbsolutePath());
		Config config = ConfigFactory.parseFile(metricTypesFile);
		String pluginId = config.getString("pluginId");
		Map<String, DPClassDescriptor> dpClassesConfiguration = new HashMap<String, DPClassDescriptor>();

		for (Config metricconfig : config.getConfigList("MetricClasses")) {
			String dpClassName = metricconfig.getString("class");
			String eventType = metricconfig.getString("eventType");
			DPClassDescriptor dpClassConfig = new DPClassDescriptor();
			dpClassesConfiguration.put(dpClassName, dpClassConfig);
			dpClassConfig.setEventType(eventType);
			
			List<String> stringAttributesList = metricconfig.getStringList("stringAttributes");
			String[] stringAttributes = stringAttributesList.toArray(new String[stringAttributesList.size()]);
			dpClassConfig.setStringAttributes(stringAttributes);
			
			if (metricconfig.hasPath("intAttributes")) {
				List<String> intAttributesList = metricconfig.getStringList("intAttributes");
				String[] intAttributes = intAttributesList.toArray(new String[intAttributesList.size()]);
				dpClassConfig.setIntAttributes(intAttributes);
			}
			
			if (metricconfig.hasPath("longAttributes")) {
				List<String> longAttributesList = metricconfig.getStringList("longAttributes");
				String[] longAttributes = longAttributesList.toArray(new String[longAttributesList.size()]);
				dpClassConfig.setLongAttributes(longAttributes);
			}
			
			if (metricconfig.hasPath("longAttributesDelta")) {
				List<String> longAttributesDeltaList = metricconfig.getStringList("longAttributesDelta");
				String[] longAttributesDelta = longAttributesDeltaList.toArray(new String[longAttributesDeltaList.size()]);
				dpClassConfig.setLongAttributesDelta(longAttributesDelta);
			}
			
			if (metricconfig.hasPath("floatAttributes")) {
				List<String> floatAttributesList = metricconfig.getStringList("floatAttributes");
				String[] floatAttributes = floatAttributesList.toArray(new String[floatAttributesList.size()]);
				dpClassConfig.setFloatAttributes(floatAttributes);
			}
		}
		metricSetConfigurationTable.put(pluginId, dpClassesConfiguration);
	}
	
}
