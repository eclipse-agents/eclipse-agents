package org.eclipse.mcp.test.junit.plugin.extension;

import java.util.Arrays;

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

	@Tool (id = "junit.MCPToolFactory.helloWorld")
	public String[] helloWorld(
			@ToolArg(name = "b1", description = "example boolean 1")
			Boolean b,
			@ToolArg(name = "c1", description = "example character 1")
			Character c,
			@ToolArg(name = "s1", description = "example String s1")
			String s,
			@ToolArg(name = "d1", description = "example double 1")
			Double d,
			@ToolArg(name = "f1", description = "example float 1")
			Float f,
			@ToolArg(name = "i1", description = "example integer 1")
			Integer i,
			@ToolArg(name = "l1", description = "example long 1")
			Long l,
			@ToolArg(name = "sh1", description = "example short 1")
			Short sh,
			@ToolArg(name = "as1", description = "example string array 1")
			String[] as,
			@ToolArg(name = "ai1", description = "example int array 1")
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