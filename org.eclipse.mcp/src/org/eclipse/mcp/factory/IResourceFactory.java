package org.eclipse.mcp.factory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mcp.IResourceController;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpServerFeatures.SyncResourceSpecification;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceRequest;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.Resource;
import io.modelcontextprotocol.spec.McpSchema.ResourceContents;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;

/**
 * Interface for components that may dynamically add and remove MCP resources to the server
 */
public interface IResourceFactory extends IFactory {
	
	/**
	 * Used to forward the controller and to add any resources to the new server's initial, empty state
	 * Called when the server starts, but also when a server restarts due to a configuration change.
	 * Use this to add any resources that should exist on the newly started/restarted server
	 * @param controller
	 */
	public abstract void initialize(IResourceController controller);

	public default SyncResourceSpecification createResourceSpec(Resource resource) {
		return new McpServerFeatures.SyncResourceSpecification(resource, this::readResource); 
	}
	
	/**
	 * @param uri
	 * @return mime type of this uri.  Default is text/plain
	 */
	public default String getResourceMimeType(String uri) {
		return "text/plain";
	}
	
	public default ReadResourceResult readResource(McpSyncServerExchange exchange, ReadResourceRequest request) {
		List<ResourceContents> contents = new ArrayList<ResourceContents>();
		for (String s: readResource(request.uri())) {
			contents.add(new TextResourceContents(
				request.uri(),
				getResourceMimeType(request.uri()),
				s));
		}
		return new ReadResourceResult(contents);
	}
	
	/**
	 * Simplistic helper method for turning a URI to an String[] of content
	 * For advanced operations, see the other <code>readResource<code> method
	 * @param url
	 * @return
	 */
	public String[] readResource(String url);
}