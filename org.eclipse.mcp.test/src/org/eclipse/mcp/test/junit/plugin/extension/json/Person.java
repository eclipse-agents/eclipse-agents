package org.eclipse.mcp.test.junit.plugin.extension.json;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Person {

	public Person() {}
	
	@JsonProperty(required = false)
    UUID id;
    
	@JsonProperty(defaultValue = "Jeremy")
    String name;
    
	@JsonProperty(required = false)
	String surname;
    
	@JsonProperty
    Address address;
    
    @JsonProperty(required = false)
    Date createdAt;
    
    @JsonProperty(required = false)
    public Person[] parents;

    Object nonJsonProperty;
    
    public void setName(String name) {
    	this.name = name;
    }

    public String getName() {
    	return name;
    }

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Person[] getParents() {
		return parents;
	}

	public void setParents(Person[] parents) {
		this.parents = parents;
	}
}
