package com.ibm.systemz.db2.mcp.tools;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.ibm.systemz.db2.mcp.AbstractTool;
import com.ibm.systemz.db2.mcp.Server;

import io.modelcontextprotocol.spec.McpSchema.CallToolResult;

public class RunQuery extends AbstractTool {

	public RunQuery(Server server) {
		super(server);
	}

	@Override
	public String getName() {
		return "run_query";
	}

	@Override
	public String getDescription() {
		return "Run a read-only query against the Db2 for z/OS database by passing in parameters for a connection-uuid and a query";
	}

	@Override
	public String getSchema() {
		return """
				{
					"type": "object",
					"properties": {
						"connection-uuid": {
							"type": "string"
						},
						"query": {
							"type": "string"
						}
					},
					"required": ["connection-uuid", "query"]
				}
				""";
	}

	@Override
	public CallToolResult apply(Map<String, Object> arguments) {
		
		CallToolResult result = null;
		Object query = arguments.get("query");
	 	
    	try {
    		Statement s = server.getConnection().createStatement();
    		result = toResult(s.executeQuery(query.toString()));

		} catch (SQLException e) {
			result = toResult(e);
		}
        return result;
	}

}
