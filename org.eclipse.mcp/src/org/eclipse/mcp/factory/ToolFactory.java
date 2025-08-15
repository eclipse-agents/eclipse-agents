package org.eclipse.mcp.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
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

/**
 * Factory for contributing a single MCP tool
 * See MCPAnnotatedToolFactory for a convenience way to create multiple tools in a file using annotations
 */
public abstract class ToolFactory implements IFactory{	

	private ListenerList<ToolVisibilityListener> listeners = new ListenerList<ToolVisibilityListener>();
	private boolean visible = true;

	/**
	 * Create the definition for your tool.
	 * see MCPAnnotatedToolFactory to automatically create tools from annotated Java methods
	 * @return
	 */
	public abstract McpSchema.Tool createTool();
	
	public SyncToolSpecification createSpec(McpSchema.Tool tool) {
		return McpServerFeatures.SyncToolSpecification.builder().tool(tool).callHandler(this::apply).build();
	}
	
	public CallToolResult apply(McpSyncServerExchange exchange, CallToolRequest req) {
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
	
	/**
	 * Simplistic method to transform a map of input arguments to a String[] response
	 * @param args
	 * @return
	 * @throws MCPException
	 */
	public abstract String[] apply(Map<String, Object> args) throws MCPException;

	/**
	 * May be used to dynamically add/remove this tool to the server.
	 * May be used in conjunction with some custom preference pages for your contributions
	 * @param visibility
	 */
	public void setVisibility(boolean visibility) {
		if (visible != visibility) {
			visible = visibility;
			for (ToolVisibilityListener listener: listeners) {
				listener.visibilityChanged(this);
			}
		}
	}
	
	public void addVisibilityListener(ToolVisibilityListener listener) {
		listeners.add(listener);
	}
	
	public void removeVisibilityListener(ToolVisibilityListener listener) {
		listeners.remove(listener);
	}
	
	public interface ToolVisibilityListener {
		public void visibilityChanged(ToolFactory factory);
	}
	
	public boolean isVisible() {
		return visible;
	}
}
