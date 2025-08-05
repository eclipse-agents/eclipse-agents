package org.eclipse.mcp.test.junit.plugin.extension;

import org.eclipse.mcp.IMCPResourceController;
import org.eclipse.mcp.IMCPResourceFactory;

public class MCPResourceController implements IMCPResourceController {

	@Override
	public void initialize(IMCPResourceFactory manager) {
		System.out.println("initialize");

	}

	@Override
	public String[] readResource(String url) {
		System.out.println("read");
		return new String[0];
	}

}

