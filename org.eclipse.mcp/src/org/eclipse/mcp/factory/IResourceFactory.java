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

public interface IResourceFactory extends IFactory{
	
	public default String getId() {
		return getClass().getCanonicalName();
	}
	
	public abstract void initialize(IResourceController controller);

	public default SyncResourceSpecification createResourceSpec(Resource resource) {
		return new McpServerFeatures.SyncResourceSpecification(resource, this::readResource); 
	}
	
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
	
	public String[] readResource(String url);

}