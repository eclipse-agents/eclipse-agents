package org.eclipse.mcp.internal;

import org.eclipse.mcp.IMCPResourceController;
import org.eclipse.mcp.IMCPResourceFactory;

import io.modelcontextprotocol.server.McpServerFeatures.SyncResourceSpecification;
import io.modelcontextprotocol.spec.McpSchema.Resource;

public class ResourceManager implements IMCPResourceController {

	ManagedServer server;
	IMCPResourceFactory factory;
	
	public ResourceManager(ManagedServer server, IMCPResourceFactory factory) {
		this.server = server;
		this.factory = factory;
		factory.initialize(this);
	}

	@Override
	public void addResource(Resource resource) {
		SyncResourceSpecification spec = factory.createResourceSpec(resource);
		server.getSyncServer().addResource(spec);
		server.getSyncServer().notifyResourcesListChanged();
	}

	@Override
	public void removeResource(String uri) {
		server.getSyncServer().removeResource(uri);
	}
}
