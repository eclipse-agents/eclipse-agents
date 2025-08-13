package org.eclipse.mcp.test.junit.plugin.extension;

import java.util.Arrays;
import java.util.List;

import org.eclipse.mcp.IMCPFactory;
import org.eclipse.mcp.IMCPResourceTemplateFactory;
import org.eclipse.mcp.IMCPResourceTemplateFactory.ResourceTemplate;
import org.eclipse.mcp.annotated.MCPAnnotatedResourceTemplateFactory;

import io.modelcontextprotocol.spec.McpSchema;

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


	@Override
	public IMCPResourceTemplateFactory[] createResourceTemplateFactories() {
		return new IMCPResourceTemplateFactory[] {
			new MyResourceTemplate()
		};
	}


	@ResourceTemplate (
			uriTemplate = "db:///{schema}/{table}",
			name = "Table",
			title = "A Database Table",
			description = "A Database Table",
			mimeType = "application/json",
			roles = {McpSchema.Role.USER, McpSchema.Role.ASSISTANT},
			priority  = 0.5)
	@ResourceTemplate (
			uriTemplate = "db:///{schema}/{table}/{column}",
			name = "Table Column",
			description = "")
	public class MyResourceTemplate extends MCPAnnotatedResourceTemplateFactory {

		@Override
		public List<String> completionReq(String argumentName, String argumentValue) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] readResource(String url) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	
}