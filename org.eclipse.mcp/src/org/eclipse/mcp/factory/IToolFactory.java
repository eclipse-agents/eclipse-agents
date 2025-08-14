package org.eclipse.mcp.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.mcp.MCPException;
import org.eclipse.mcp.internal.Tracer;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.TextContent;

public interface IToolFactory extends IFactory{	

	/**
	 * @return Unique identifier reference-able in <code>org.eclipse.mcp.modelContextProtocolServer</code> extension
	 */
	public default String getId() {
		return getClass().getCanonicalName();
	}
	
	public String getCategory();

	public McpSchema.Tool createTool();
	
	public default SyncToolSpecification createSpec(McpSchema.Tool tool) {
		return McpServerFeatures.SyncToolSpecification.builder().tool(tool).callHandler(this::apply).build();
	}
	
	public default CallToolResult apply(McpSyncServerExchange exchange, CallToolRequest req) {
		CallToolResult result = null;
		List<Content> content = new ArrayList<Content>();
		
		try {
			String[] rawText = apply(req.arguments());
			if (rawText != null) {
				for (String s: rawText) {
					content.add(new TextContent(s));
				}
			} else {
				Tracer.trace().trace(Tracer.IMPLEMENTATIONS, 
						"org.eclipse.mcp.IMCPToolFactory.apply(Map<String, Object>) returned null");
			}
			result = new CallToolResult(content, false);
		} catch (Exception e) {
			content.add(new TextContent(e.getLocalizedMessage()));
			e.printStackTrace();
			result = new CallToolResult(content, true);
		}
		return result;
	}
	
	public String[] apply(Map<String, Object> args) throws MCPException;

}
