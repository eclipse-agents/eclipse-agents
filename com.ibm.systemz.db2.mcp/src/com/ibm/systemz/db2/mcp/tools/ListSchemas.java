package com.ibm.systemz.db2.mcp.tools;

import java.util.Map;

import com.ibm.systemz.db2.mcp.AbstractTool;
import com.ibm.systemz.db2.mcp.Server;

import io.modelcontextprotocol.spec.McpSchema.CallToolResult;

public class ListSchemas extends AbstractTool {

	public ListSchemas(Server server) {
		super(server);
	}

	@Override
	public String getName() {
		return "list_schemas";
	}

	@Override
	public String getDescription() {
		return "List schemas for Db2 for z/OS database";
	}

	@Override
	public String getSchema() {
		return """
				{
				"type": "object",
				"properties": {
					"connection-uuid": {
						"type": "string"
					}
				},
				"required": ["connection-uuid"]
			}
			""";
	}

	@Override
	public CallToolResult apply(Map<String, Object> arguments) {
		
		String connectionUUID = arguments.get("connection-uuid").toString();
		String sqlStatement = "SELECT DISTINCT(CREATOR) AS SCHEMA FROM SYSIBM.SYSTABLES LIMIT 10;";
		return server.runQuery(connectionUUID, sqlStatement);
	}

}
