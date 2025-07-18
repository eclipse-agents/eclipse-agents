package org.eclipse.mcp.internal;

public class ServerManager {

	public ServerManager(ExtensionManager extensionManager) {
		
		for (ExtensionManager.Server server: extensionManager.getServers()) {
			new ManagedServer(server).start();
		}
	}
}
