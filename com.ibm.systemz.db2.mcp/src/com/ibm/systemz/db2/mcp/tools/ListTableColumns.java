package com.ibm.systemz.db2.mcp.tools;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import com.ibm.db2.catanavi.CataNaviQuery;
import com.ibm.systemz.db2.mcp.AbstractTool;
import com.ibm.systemz.db2.mcp.Server;

import io.modelcontextprotocol.spec.McpSchema.CallToolResult;

public class ListTableColumns extends AbstractTool {

	public ListTableColumns(Server server) {
		super(server);
	}

	@Override
	public String getName() {
		return "get_table_columns";
	}

	@Override
	public String getDescription() {
		return "Get columns of a Db2 for z/OS table";
	}

	@Override
	public String getSchema() {
		return """
				{
					"type": "object",
					"properties": {
						"schema": {
							"type": "string"
						},
						"table": {
							"type": "string"
						}
					},
					"required": ["schema", "table"]
				}
				""";
	}

	@Override
	public CallToolResult apply(Map<String, Object> arguments) {
		
		String schema = arguments.get("schema").toString();
		String table = arguments.get("table").toString();
		
		CallToolResult result = null;
	 	
//    	try {
//    		PreparedStatement s = server.getConnection().prepareStatement(CataNaviQuery.TABLE_COLUMNS);
//    		s.setString(1, schema);
//    		s.setString(2, table);
//    		result = toResult(s.executeQuery());
//
//		} catch (SQLException e) {
//			result = toResult(e);
//		}
        return result;
	}

}
