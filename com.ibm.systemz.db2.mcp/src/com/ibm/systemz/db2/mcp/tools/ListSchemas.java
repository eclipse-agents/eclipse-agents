package com.ibm.systemz.db2.mcp.tools;

import java.sql.SQLException;
import java.sql.Statement;
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
		
		CallToolResult result = null;
	 	
    	try {
    		Statement s = server.getConnection().createStatement();
			result = toResult(s.executeQuery("SELECT DISTINCT(CREATOR) AS SCHEMA FROM SYSIBM.SYSTABLES;"));
		} catch (SQLException e) {
			result = toResult(e);
		}
        return result;
	}

}
