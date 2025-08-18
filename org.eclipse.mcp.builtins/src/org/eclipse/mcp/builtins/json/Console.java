package org.eclipse.mcp.builtins.json;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonClassDescription("An Eclipse IDE console")
public class Console {

	@JsonProperty()
	@JsonPropertyDescription("Console name")
	String name;
	@JsonProperty()
	int id;
	@JsonProperty()
	String type;
	
	public Console(String name, int id, String type) {
		super();
		this.name = name;
		this.id = id;
		this.type = type;
	}
	
}
