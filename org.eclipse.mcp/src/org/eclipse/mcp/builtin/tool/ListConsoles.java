package org.eclipse.mcp.builtin.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.mcp.AbstractTool;
import org.eclipse.mcp.Server;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.TextContent;

public class ListConsoles extends AbstractTool {

	public ListConsoles(Server server) {
		super(server);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return "eclipse-list-consoles";
	}

	@Override
	public String getDescription() {
		return "Return a list of active consoles";
	}

	@Override
	public String getSchema() {
		return """
				{
				"type": "object",
				"properties": {
				},
				"required": []
			}
			""";
	}
	

	@Override
	public CallToolResult apply(McpSyncServerExchange t, Map<String, Object> u) {
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		List<Content> result = new ArrayList<Content>();
		for (IConsole console: manager.getConsoles()) {
			result.add(new TextContent(console.getName()));
		}
		
		return new CallToolResult(result, true);
	}

}
