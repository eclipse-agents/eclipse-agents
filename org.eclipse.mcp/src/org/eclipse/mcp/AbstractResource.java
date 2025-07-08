package org.eclipse.mcp;


import java.util.function.Function;

import io.modelcontextprotocol.spec.McpSchema.ReadResourceRequest;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;

public abstract class AbstractResource implements Function<ReadResourceRequest, ReadResourceResult> {
	
	protected Server server;
	
	public AbstractResource(Server server) {
		this.server = server;
	}
}
