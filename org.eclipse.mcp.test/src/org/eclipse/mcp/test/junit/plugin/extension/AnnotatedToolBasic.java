package org.eclipse.mcp.test.junit.plugin.extension;

import java.lang.reflect.Method;

import org.eclipse.mcp.experimental.annotated.MCPAnnotatedToolFactory;

public class AnnotatedToolBasic extends MCPAnnotatedToolFactory {

	public AnnotatedToolBasic(Method method, Tool toolAnnotation) {
		super(method, toolAnnotation);
	}

	@Tool (description = "Greets user with a hello", 
			name = "test-hello-world-basic", 
			title = "Test Hello World")
	public String helloWorld() {
		return "Hello";
	}
}
