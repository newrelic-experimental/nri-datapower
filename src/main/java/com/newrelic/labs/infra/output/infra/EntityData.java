package com.newrelic.labs.infra.output.infra;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EntityData {

	private String name;
	
	private String type;
	
	@JsonInclude(Include.NON_NULL)
    @JsonProperty("id_attributes")
	private List<Attribute> idAttributes;
	
	public EntityData(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Attribute> getIdAttributes() {
		return idAttributes;
	}

	public void setIdAttributes(List<Attribute> idAttributes) {
		this.idAttributes = idAttributes;
	}

	
}
