package org.eclipse.mcp.internal;

public class ServerManager {

	public ServerManager(ExtensionManager extensionManager) {
		
		for (ExtensionManager.Server server: extensionManager.getServers()) {
			Tracer.trace().trace(Tracer.DEBUG, "HELLO" + server.getName());
			Tracer.trace().trace(Tracer.IMPLEMENTATIONS, "GOODBYE" + server.getName());
			new ManagedServer(server).start();
		}
	}
}
