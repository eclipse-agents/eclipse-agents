package com.ibm.systemz.db2.mcp.tools;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import com.ibm.db2.catanavi.CataNaviQuery;
import com.ibm.systemz.db2.mcp.AbstractTool;
import com.ibm.systemz.db2.mcp.Server;

import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.LoggingLevel;

public class ListTables extends AbstractTool {

	public ListTables(Server server) {
		super(server);
	}

	@Override
	public String getName() {
		return "list_tables";
	}

	@Override
	public String getDescription() {
		return "List Db2 for z/OS tables for a particular schema";
	}

	@Override
	public String getSchema() {
		return """
				{
				"type": "object",
				"properties": {
					"schema-filter": {
						"type": "string",
						"description": "If present, only tables whose schema matches this LIKE expression will be returned."
					},
					"table-filter": {
						"type": "string",
						"description": "If present, only tables whose name matches this LIKE expression will be returned."
					}
				},
				"required": []
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
		
		Object schema = arguments.get("schema-filter");
		Object table = arguments.get("table-filter");
		
		CallToolResult result = null;
	 	

//		StringBuffer filters = new StringBuffer();
//		if (table != null) {
//			filters.append(" AND TB1.NAME LIKE ?");
//		}
//		if (schema != null) {
//			filters.append(" AND TB1.CREATOR LIKE ?");
//		}
//		
//    	try {
//    		String query = String.format(CataNaviQuery.TABLE_LIST, filters);
//    		server.log(LoggingLevel.INFO, this, query);
//    		PreparedStatement s = server.getConnection().prepareStatement(query);
//    		
//    		if (table != null) {
//    			s.setString(1,  table.toString());
//    		}
//    		if (schema != null) {
//    			if (table != null) {
//    				s.setString(2,  schema.toString());
//    			} else {
//    				s.setString(1,  schema.toString());
//    			}
//    		}
//			
//    		result = toResult(s.executeQuery());
//
//		} catch (SQLException e) {
//			server.log(this, e.getLocalizedMessage(), e);
//			result = toResult(e);
//		}
        return result;
	}

}
