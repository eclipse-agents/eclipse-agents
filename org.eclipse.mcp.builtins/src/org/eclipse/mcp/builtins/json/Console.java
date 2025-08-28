package org.eclipse.mcp.builtins.json;

import org.eclipse.mcp.builtin.resource.ConsoleAdapter;
import org.eclipse.ui.console.IConsole;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonClassDescription("An Eclipse IDE console")
public class Console {

	@JsonProperty()
	@JsonPropertyDescription("Console name")
	String name;
	@JsonProperty()
	String type;
	@JsonProperty()
	String uri;
	
	public Console(IConsole console) {
		super();
		this.name = console.getName();
		this.type = console.getType();
		
		this.uri = new ConsoleAdapter(console).toUri();
	}
}
