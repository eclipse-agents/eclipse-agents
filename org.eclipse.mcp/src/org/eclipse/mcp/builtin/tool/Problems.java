package org.eclipse.mcp.builtin.tool;

import java.util.Map;

import org.eclipse.mcp.AbstractTool;
import org.eclipse.mcp.Server;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;

public class Problems extends AbstractTool {

	public Problems() {
	}

	@Override
	public String getName() {
		return "list-eclipse-problems";
	}

	@Override
	public String getDescription() {
		return "List the problems in the workspace of a running Eclipse Integrated Development Environment";
	}

	@Override
	public String getSchema() {
		return """
				{
				"type": "object",
				"properties": {
				},
				"required": []
			}
			""";
	}

	@Override
	public String[] apply(Map<String, Object> t) {
		// TODO Auto-generated method stub
		return null;
	}}
