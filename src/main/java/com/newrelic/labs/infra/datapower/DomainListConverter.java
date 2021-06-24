package com.newrelic.labs.infra.datapower;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.IStringConverter;

public class DomainListConverter implements IStringConverter<List<Domain>> {
	@Override
	public List<Domain> convert(String files) {
		String[] domains = files.split(",");
		List<Domain> domainsList = new ArrayList<>();
		for (String domain : domains) {
			
		}
		return domainsList;
	}
}