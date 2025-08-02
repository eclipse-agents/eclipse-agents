package org.eclipse.mcp.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.mcp.IMCPResourceController;
import org.eclipse.mcp.IMCPResourceFactory;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpServerFeatures.SyncResourceSpecification;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceRequest;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.Resource;
import io.modelcontextprotocol.spec.McpSchema.ResourceContents;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;

public class ResourceManager implements IMCPResourceFactory {

	ManagedServer server;
	IMCPResourceController factory;
	
	public ResourceManager(ManagedServer server, IMCPResourceController factory) {
		this.server = server;
		this.factory = factory;
		factory.initialize(this);
	}

	@Override
	public UUID addResource(String url, String name, String description, String mimeType) {
		Resource resource = new Resource(url, name, description, mimeType, null);
		SyncResourceSpecification spec = new McpServerFeatures.SyncResourceSpecification(resource, 
				(McpSyncServerExchange exchange, ReadResourceRequest request) -> {
					List<ResourceContents> contents = new ArrayList<ResourceContents>();
					for (String s: factory.readResource(request.uri())) {
						contents.add(new TextResourceContents(
							url,
							mimeType,
							s));
					}
							
					return new ReadResourceResult(null);
				});
	
		server.getSyncServer().addResource(spec);
		server.getSyncServer().notifyResourcesListChanged();
		return null;
	}

	@Override
	public void removeResource(String uri) {
		server.getSyncServer().removeResource(uri);
	}
}
