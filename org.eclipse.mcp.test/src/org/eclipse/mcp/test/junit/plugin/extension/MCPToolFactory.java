package org.eclipse.mcp.test.junit.plugin.extension;

import org.eclipse.mcp.IElementProperties;
import org.eclipse.mcp.IMCPToolFactory;


/*
{
  "type": "object",
  "properties": {
    "name": {
      "type": "string"
    }
  },
  "required": ["name"]
}
*/



public class MCPToolFactory implements IMCPToolFactory {

	@Tool (id = "")
	public String[] helloWorld(
			@ToolArg(name = "firstName", description = "Users first name")
			String firstName, 
			@ToolArg(name = "lastName", description = "Users last name")
			String lastName) {
		return new String[] {
			"Hello " + firstName,
			"Goodbye" + lastName
		};
	}

	@Override
	public void initialize(IElementProperties properties) {
		// TODO Auto-generated method stub
		
	}
}