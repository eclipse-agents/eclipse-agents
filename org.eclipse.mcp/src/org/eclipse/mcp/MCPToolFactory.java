package org.eclipse.mcp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.TextContent;

import io.modelcontextprotocol.spec.McpSchema.Tool;

public abstract class MCPToolFactory {

	
	public MCPToolFactory() {
		super();
	}

	/**
	 * @return Unique identifier reference-able in <code>org.eclipse.mcp.modelContextProtocolServer</code> extension
	 */
	public String getId() {
		return getClass().getCanonicalName();
	}
	
	public abstract String getCategory();

	public abstract Tool createTool();
	
	public SyncToolSpecification createSpec(Tool tool) {
		return McpServerFeatures.SyncToolSpecification.builder().tool(tool).callHandler(this::apply).build();
	}
	
	public CallToolResult apply(McpSyncServerExchange exchange, CallToolRequest req) {
		CallToolResult result = null;
		List<Content> content = new ArrayList<Content>();
		
		try {
			String[] rawText = apply(req.arguments());
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
	
	public abstract String[] apply(Map<String, Object> args);

}
