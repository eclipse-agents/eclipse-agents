package org.eclipse.mcp;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.Tool;

public abstract class AbstractTool implements Function<Map<String, Object>, String[]> {
	
	public AbstractTool() {
	}
	
	public abstract String getName();
	public abstract String getDescription();
	public abstract String getSchema();
	
	
	SyncToolSpecification getSpecification() {
		Tool tool = new Tool(getName(), getDescription(), getSchema());				
		return new SyncToolSpecification(tool, new BiFunction<McpSyncServerExchange, Map<String, Object>, McpSchema.CallToolResult>() {
			@Override
			public CallToolResult apply(McpSyncServerExchange t, Map<String, Object> u) {
				CallToolResult result = null;
				List<Content> content = new ArrayList<Content>();
				try {
					String[] rawText = AbstractTool.this.apply(u);
					for (String s: rawText) {
						content.add(new TextContent(s));
					}
					result = new CallToolResult(content, false);
				} catch (Exception e) {
					content.add(new TextContent(e.getLocalizedMessage()));
					e.printStackTrace();
					result = new CallToolResult(content, true);
				}
				return result;
			}
		});
	}
}
