package org.eclipse.mcp.builtins.json;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonClassDescription("List of Eclipse IDE text editors")
public class Editors {
	
	@JsonProperty
	public Editor[] editors;
}
