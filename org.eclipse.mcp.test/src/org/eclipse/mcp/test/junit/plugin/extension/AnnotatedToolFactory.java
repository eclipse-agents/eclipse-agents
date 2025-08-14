package org.eclipse.mcp.test.junit.plugin.extension;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.eclipse.mcp.experimental.annotated.MCPAnnotatedToolFactory;

public class AnnotatedToolFactory extends MCPAnnotatedToolFactory {

	public AnnotatedToolFactory(Method method, Tool toolAnnotation) {
		super(method, toolAnnotation);
	}

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
}
