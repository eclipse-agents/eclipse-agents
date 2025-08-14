package org.eclipse.mcp.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.mcp.factory.IFactory;

public class ServerManager {

	public ServerManager(ExtensionManager extensionManager) {
		
		List<IFactory> factories = new ArrayList<IFactory>();
		
		for (ExtensionManager.Contributor contributor: extensionManager.getContributors()) {
			factories.addAll(Arrays.asList(contributor.getFactories()));
		}

		new ManagedServer("Eclipse MCP Server", "Default Eclipse MCP Server", 2834, factories.toArray(IFactory[]::new)).start();

	}
}
