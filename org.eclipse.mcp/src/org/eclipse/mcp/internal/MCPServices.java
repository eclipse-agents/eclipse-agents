package org.eclipse.mcp.internal;

import org.eclipse.mcp.IMCPServices;
import org.eclipse.osgi.service.debug.DebugTrace;

import io.modelcontextprotocol.server.McpServerFeatures.SyncResourceSpecification;

public class MCPServices implements IMCPServices {

	MCPServer server;
	
	public MCPServices(MCPServer server) {
		this.server = server;
	}

	@Override
	public void addResource(SyncResourceSpecification spec) {
		server.addResource(spec);
	}

	@Override
	public void removeResource(String uri) {
		server.removeResource(uri);
	}

	@Override
	public boolean getToolVisibility(String toolName) {
		return server.getVisibility(toolName);
	}

	@Override
	public boolean setToolVisibility(String toolName, boolean isVisible) {
		return server.setVisibility(toolName, isVisible);
	}

	@Override
	public DebugTrace getTracer() {
		return Tracer.trace();
	}
}
