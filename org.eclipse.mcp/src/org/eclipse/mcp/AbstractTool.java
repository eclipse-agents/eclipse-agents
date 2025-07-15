package org.eclipse.mcp;


import java.util.Map;
import java.util.function.BiFunction;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

public abstract class AbstractTool implements BiFunction<McpSyncServerExchange, Map<String, Object>, McpSchema.CallToolResult> {
	
	public AbstractTool() {
	}
	
	public abstract String getName();
	public abstract String getDescription();
	public abstract String getSchema();
}
