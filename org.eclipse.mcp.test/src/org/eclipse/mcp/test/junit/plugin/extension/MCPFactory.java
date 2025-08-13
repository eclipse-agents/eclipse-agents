package org.eclipse.mcp.test.junit.plugin.extension;

import java.util.Arrays;

import org.eclipse.mcp.IMCPFactory;


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



public class MCPFactory implements IMCPFactory {

	@Tool (id = "junit.MCPToolFactory.helloWorld", 
			description = "Greets user with a hello", 
			name = "test-hello-world")
	public String[] helloWorld(
			@ToolArg(name = "b1", description = "boolean")
			Boolean b,
			@ToolArg(name = "c1", description = "character")
			Character c,
			@ToolArg(name = "s1", description = "string")
			String s,
			@ToolArg(name = "d1", description = "double")
			Double d,
			@ToolArg(name = "f1", description = "float")
			Float f,
			@ToolArg(name = "i1", description = "integer")
			Integer i,
			@ToolArg(name = "l1", description = "long")
			Long l,
			@ToolArg(name = "sh1", description = "short")
			Short sh,
			@ToolArg(name = "as1", description = "string array")
			String[] as,
			@ToolArg(name = "ai1", description = "int array")
			Integer[] ai
			
			) {
		return new String[] {
			"\n" + b,
			"\n" + c,
			"\n" + s,
			"\n" + d,
			"\n" + f,
			"\n" + i,
			"\n" + l,
			"\n" + sh,
			"\n" + Arrays.toString(as),
			"\n" + Arrays.toString(ai),
		};
	}
	
//	@Tool (id = "junit.MCPToolFactory.helloWorld2")
//	public String[] helloWorld2(
//			@ToolArg(name = "b1", description = "example boolean 1")
//			boolean b,
//			@ToolArg(name = "c1", description = "example character 1")
//			char c,
//			@ToolArg(name = "s1", description = "example String s1")
//			String s,
//			@ToolArg(name = "d1", description = "example double 1")
//			double d,
//			@ToolArg(name = "f1", description = "example float 1")
//			float f,
//			@ToolArg(name = "i1", description = "example integer 1")
//			int i,
//			@ToolArg(name = "l1", description = "example long 1")
//			long l,
//			@ToolArg(name = "sh1", description = "example short 1")
//			short sh
//			) {
//		return new String[] {
//			"\n" + b,
//			"\n" + c,
//			"\n" + s,
//			"\n" + d,
//			"\n" + f,
//			"\n" + i,
//			"\n" + l,
//			"\n" + sh
//		};
//	}

}