package org.eclipse.mcp.test.junit.plugin.extension;

import org.eclipse.mcp.IMCPResourceController;
import org.eclipse.mcp.IMCPResourceFactory;

import io.modelcontextprotocol.spec.McpSchema.Resource;

public class MCPResourceFactory extends IMCPResourceFactory {

	@Override
	public void initialize(IMCPResourceController controller) {
		Resource resource = new Resource(MCPResourceFactory.class.getCanonicalName(), "name", "desc", "mime", null);
		controller.addResource(resource);
		controller.removeResource(MCPResourceFactory.class.getCanonicalName());
		
	}

	@Override
	public String[] readResource(String url) {
		if (MCPResourceFactory.class.getCanonicalName().equals(url)) {
			return new String[] { "Hello", "World" };
		}
		return new String[0];
	}

	
}

