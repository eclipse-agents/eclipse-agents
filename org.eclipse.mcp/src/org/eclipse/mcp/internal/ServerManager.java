package org.eclipse.mcp.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mcp.IMCPFactory;

public class ServerManager {

	public ServerManager(ExtensionManager extensionManager) {
		
		List<IMCPFactory> enabledFactories = new ArrayList<IMCPFactory>();
		for (ExtensionManager.Factory f: extensionManager.getFactories()) {
			enabledFactories.add(f.implementation);
		}
		
		new ManagedServer("Eclipse MCP Server", "Default Eclipse MCP Server", 2834, enabledFactories.toArray(new IMCPFactory[0])).start();

	}
}
