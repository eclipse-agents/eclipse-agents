package com.ibm.systemz.db2.mcp.tools;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.ibm.systemz.db2.ide.ConnectionEnvironment;
import com.ibm.systemz.db2.ide.ConnectionSummary;
import com.ibm.systemz.db2.ide.ConnectionSummary.KIND;
import com.ibm.systemz.db2.mcp.AbstractTool;
import com.ibm.systemz.db2.mcp.Server;

import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.TextContent;

public class ListConnections extends AbstractTool {

	public ListConnections(Server server) {
		super(server);
	}

	@Override
	public String getName() {
		return "list_connections";
	}

	@Override
	public String getDescription() {
		return "List the available Db2 for z/OS connections";
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
	public CallToolResult apply(Map<String, Object> arguments) {
		
		List<Content> result = new ArrayList<Content>();
		
		for (ConnectionSummary summary: ConnectionEnvironment.getLocationSummaries()) {
		
			JsonObject o = new JsonObject();
			if (KIND.db2.equals(summary.getKind())) {
				o.addProperty("connection-uuid", summary.getId().toString());
				o.addProperty("name", summary.getName());
			}
				
			result.add(new TextContent(o.toString()));
		}
		
		return new CallToolResult(result, false);
	}

}
