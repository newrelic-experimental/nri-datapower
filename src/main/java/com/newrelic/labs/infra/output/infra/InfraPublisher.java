package com.newrelic.labs.infra.output.infra;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newrelic.labs.infra.agent.MetricSet;
import com.newrelic.labs.infra.agent.Publisher;

public class InfraPublisher extends Publisher {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void publish(String entityName, String entityType, Collection<MetricSet> metricSets) {
		Payload payload = new Payload();
		payload.setName("com.newrelic.labs.iib");
		payload.setIntegrationVersion("0.9.0");
		payload.setProtocolVersion("3");
		List<EntityItem> entityitems = new LinkedList<EntityItem>();
		payload.setData(entityitems);
		//
		EntityItem entityitem = new EntityItem();
		entityitems.add(entityitem);
		EntityData entityData = new EntityData(entityName, entityType);
		//add any extra tags
		/*
		List<Attribute> idAttributes = new LinkedList<Attribute>();
		idAttributes.add(new Attribute("env", "prod"));
		entityData.setIdAttributes(idAttributes);
		*/
		
		entityitem.setEntity(entityData);
		List<Map<String, Object>> metricDataList = new LinkedList<Map<String, Object>>();
		entityitem.setMetrics(metricDataList );
		
		for (Iterator<MetricSet> iterator = metricSets.iterator(); iterator.hasNext();) {
			MetricSet metricSet = iterator.next();
			
			Map<String, Object> metricData = new HashMap<String, Object>();
			metricDataList.add(metricData);
			metricData.put("event_type", metricSet.getMetricSetName());
			for (Iterator<String> msIter = metricSet.getMetrics().keySet().iterator(); msIter.hasNext();) {
				String metricKey = msIter.next();
				metricData.put(metricKey, metricSet.getMetrics().get(metricKey));
			}
			for (Iterator<String> attrIter = metricSet.getAttributes().keySet().iterator(); attrIter.hasNext();) {
				String attrKey = attrIter.next();
				metricData.put(attrKey, metricSet.getAttributes().get(attrKey));
			}
		}
		
		try {
			objectMapper.writeValue(System.out, payload);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void shutdown() {
		
	}

}
