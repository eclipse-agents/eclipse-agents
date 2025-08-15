package org.eclipse.mcp.test.junit.plugin.extension.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 */
public class Address {
	
	public Address() {}

	@JsonProperty(required = false)
	String[] street;
    @JsonProperty(required = false)
    String city;
    @JsonProperty(required = false)
    String state;
    @JsonProperty(required = false)
    String zip;

	public String[] getStreet() {
		return street;
	}
	public void setStreet(String[] street) {
		this.street = street;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
    
    
}
