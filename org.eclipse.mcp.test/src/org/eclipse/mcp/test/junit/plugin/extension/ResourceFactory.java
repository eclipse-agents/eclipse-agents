package org.eclipse.mcp.test.junit.plugin.extension;

import org.eclipse.mcp.IResourceController;
import org.eclipse.mcp.factory.IResourceFactory;

import io.modelcontextprotocol.spec.McpSchema;

public class ResourceFactory implements IResourceFactory {

	@Override
	public void initialize(IResourceController controller) {
		controller.addResource(McpSchema.Resource.builder()
				.uri(ResourceFactory.class.getCanonicalName())
				.name("name")
				.description("description")
				.mimeType("plain/text")
				.build());
		controller.removeResource(ResourceFactory.class.getCanonicalName());
		
	}

	@Override
	public String[] readResource(String url) {
		if (ResourceFactory.class.getCanonicalName().equals(url)) {
			return new String[] { "Hello", "World" };
		}
		return new String[0];
	}
}

