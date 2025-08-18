package org.eclipse.mcp.builtins.json;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonClassDescription("List of Eclipse IDE consoles")
public class Consoles {
	@JsonProperty
	public Console[] consoles;
}
