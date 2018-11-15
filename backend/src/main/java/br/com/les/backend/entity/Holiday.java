package br.com.les.backend.entity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Component
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"date",
"name",
"type"
})
public class Holiday extends DomainEntity{

	@Transient
	@JsonProperty("date")
	private String jsonDate;
	
	private LocalDate date;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("type")
	private String type;
	
	@Transient
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	@JsonProperty("date")
	public String getJsonDate() {
	return jsonDate;
	}
	
	@JsonProperty("date")
	public void setDate(String jsonDate) {
	this.jsonDate = jsonDate;
	}
	
	@JsonProperty("name")
	public String getName() {
	return name;
	}
	
	@JsonProperty("name")
	public void setName(String name) {
	this.name = name;
	}
	
	@JsonProperty("type")
	public String getType() {
	return type;
	}
	
	@JsonProperty("type")
	public void setType(String type) {
	this.type = type;
	}
	
	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
	return this.additionalProperties;
	}
	
	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
	this.additionalProperties.put(name, value);
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

}
