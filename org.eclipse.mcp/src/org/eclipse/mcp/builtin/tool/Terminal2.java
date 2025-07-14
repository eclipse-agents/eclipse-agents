package org.eclipse.mcp.builtin.tool;

import java.util.Map;

import org.eclipse.mcp.AbstractTool;
import org.eclipse.mcp.Server;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsoleManager;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;

public class Terminal2 extends AbstractTool {

	public Terminal2(Server server) {
		super(server);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSchema() {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public CallToolResult apply(McpSyncServerExchange t, Map<String, Object> u) {
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		manager.getConsoles();
		return null;
	}

}
