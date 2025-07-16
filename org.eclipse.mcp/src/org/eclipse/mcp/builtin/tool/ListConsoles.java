package org.eclipse.mcp.builtin.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.mcp.AbstractTool;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.TextContent;

public class ListConsoles extends AbstractTool {

	Gson gson;
	public ListConsoles() {
		super();
		gson = new Gson();
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
	public String[] apply(Map<String, Object> u) {
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		List<String> result = new ArrayList<String>();
		for (IConsole console: manager.getConsoles()) {
			Console c = new Console(console.getName(), console.hashCode(),  console.getType());
			result.add(gson.toJson(c));
		}
		return result.toArray(new String[0]);
	}
	
	class Console {
		String name;
		int id;
		String type;
		
		public Console(String name, int id, String type) {
			super();
			this.name = name;
			this.id = id;
			this.type = type;
		}
	}

}
