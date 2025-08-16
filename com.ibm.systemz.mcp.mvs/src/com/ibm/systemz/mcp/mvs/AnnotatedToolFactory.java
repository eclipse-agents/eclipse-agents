package com.ibm.systemz.mcp.mvs;

import java.lang.reflect.Method;

import org.eclipse.mcp.experimental.annotated.MCPAnnotatedToolFactory;

public class AnnotatedToolFactory extends MCPAnnotatedToolFactory {

	public AnnotatedToolFactory(Method method, Tool toolAnnotation) {
		super(method, toolAnnotation);
	}
}
