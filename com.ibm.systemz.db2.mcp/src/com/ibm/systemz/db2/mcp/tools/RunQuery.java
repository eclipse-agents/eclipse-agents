package com.ibm.systemz.db2.mcp.tools;

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
		return "Run a read-only query against the Db2 for z/OS database by passing in parameters for a connection-uuid and a query.  On success nothing will be returned.";
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
	public String[] apply(Map<String, Object> t) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public CallToolResult apply(Map<String, Object> arguments) {
		
		String connectionUUID = arguments.get("connection-uuid").toString();
		String query = arguments.get("query").toString();
		return server.runQuery(connectionUUID, query);
	}

}
