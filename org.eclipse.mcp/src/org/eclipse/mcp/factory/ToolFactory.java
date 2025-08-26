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
public abstract class ToolFactory implements IFactory {	

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
			Object response = apply(req.arguments());
			if (response != null) {
				if (response instanceof String) {
					content.add(new TextContent((String)response));
				} else if (response instanceof String[]) {
					for (String s: (String[])response) {
						content.add(new TextContent(s));
					}
				} else {
					throw new MCPException("ToolFactory.apply did not return a String or String[], but returned: " + response.getClass().getCanonicalName());
				}
			} else {
				Tracer.trace().trace(Tracer.IMPLEMENTATIONS, 
						"ToolFactory.apply(Map<String, Object>) returned null");
			}
			result = new CallToolResult(content, false);
		} catch (Exception e) {
			content.add(new TextContent(e.getLocalizedMessage()));
			Tracer.trace().trace(Tracer.IMPLEMENTATIONS, e.getLocalizedMessage(), e);
			e.printStackTrace();
			result = new CallToolResult(content, true);
		}
		return result;
	}
	
	/**
	 * Simplistic method to transform a map of input arguments to a String[] response
	 * @param args
	 * @return For simplistic responses return String or String[]
	 * @throws MCPException
	 */
	public abstract Object apply(Map<String, Object> args);

	/**
	 * May be used to dynamically add/remove this tool to the server.
	 * May be used in conjunction with some custom preference pages for your contributions
	 * @param visibility
	 */
	public final void setVisibility(boolean visibility) {
		if (visible != visibility) {
			visible = visibility;
			for (ToolVisibilityListener listener: listeners) {
				listener.visibilityChanged(this);
			}
		}
	}
	
	public final void addVisibilityListener(ToolVisibilityListener listener) {
		listeners.add(listener);
	}
	
	public final void removeVisibilityListener(ToolVisibilityListener listener) {
		listeners.remove(listener);
	}
	
	public interface ToolVisibilityListener {
		public void visibilityChanged(ToolFactory factory);
	}
	
	public final boolean isVisible() {
		return visible;
	}
}
