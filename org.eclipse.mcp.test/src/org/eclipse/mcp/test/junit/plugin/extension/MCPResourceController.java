package org.eclipse.mcp.test.junit.plugin.extension;

import org.eclipse.mcp.IMCPResourceController;
import org.eclipse.mcp.IMCPResourceFactory;

public class MCPResourceController implements IMCPResourceController {

	@Override
	public void initialize(IMCPResourceFactory manager) {
		manager.addResource(MCPResourceController.class.getCanonicalName(), "name", "desc", "mime");
		manager.removeResource(MCPResourceController.class.getCanonicalName());

	}

	@Override
	public String[] readResource(String url) {
		if (MCPResourceController.class.getCanonicalName().equals(url)) {
			return new String[] { "Hello", "World" };
		}
		return new String[0];
	}
}

