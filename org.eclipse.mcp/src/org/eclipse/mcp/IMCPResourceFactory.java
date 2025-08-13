package org.eclipse.mcp;

import java.util.ArrayList;
import java.util.List;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpServerFeatures.SyncResourceSpecification;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceRequest;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.Resource;
import io.modelcontextprotocol.spec.McpSchema.ResourceContents;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;

public abstract class IMCPResourceFactory {
	
	public String getId() {
		return getClass().getCanonicalName();
	}
	
	public abstract void initialize(IMCPResourceController controller);

	public SyncResourceSpecification createResourceSpec(Resource resource) {
		return new McpServerFeatures.SyncResourceSpecification(resource, this::readResource); 
	}
	
	public String getResourceMimeType(String uri) {
		return "text/plain";
	}
	
	public ReadResourceResult readResource(McpSyncServerExchange exchange, ReadResourceRequest request) {
		List<ResourceContents> contents = new ArrayList<ResourceContents>();
		for (String s: readResource(request.uri())) {
			contents.add(new TextResourceContents(
				request.uri(),
				getResourceMimeType(request.uri()),
				s));
		}
		return new ReadResourceResult(contents);
	}
	
	public abstract String[] readResource(String url);

}