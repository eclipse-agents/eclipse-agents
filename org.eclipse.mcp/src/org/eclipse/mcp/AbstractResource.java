package org.eclipse.mcp;


import java.util.function.Function;

import io.modelcontextprotocol.spec.McpSchema.ReadResourceRequest;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;

public abstract class AbstractResource implements Function<ReadResourceRequest, ReadResourceResult> {
	
	public AbstractResource() {
	}
}
