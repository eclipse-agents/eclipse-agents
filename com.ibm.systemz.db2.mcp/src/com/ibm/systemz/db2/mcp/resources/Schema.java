package com.ibm.systemz.db2.mcp.resources;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.systemz.db2.mcp.AbstractResource;
import com.ibm.systemz.db2.mcp.Server;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpServerFeatures.SyncResourceRegistration;
import io.modelcontextprotocol.spec.McpSchema.LoggingLevel;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceRequest;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.Resource;
import io.modelcontextprotocol.spec.McpSchema.ResourceContents;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;

public class Schema extends AbstractResource {
	String schema;
	String url;
	
	List<String> demoAllowed = null;
	
	public Schema(Server server, String schema)  {
		super(server);
		this.schema = schema;
		this.url = server.getUrl() + "/schemas/" + schema;
		Resource resource = new Resource(url, schema, "Db2 for z/OS Schema " + schema, "application/text", null);
		SyncResourceRegistration syncResourceRegistration = new McpServerFeatures.SyncResourceRegistration(resource, this);
		server.getSyncServer().addResource(syncResourceRegistration);
		
//      String[] allowed = new String[] { "SYSTABLES", "SYSCOLUMNS", "DEALERSHIP", "MAKE_MODEL", "EASTINVNTRY", "CENTINVNTRY", "PACINVNTRY" };
//		demoAllowed = new ArrayList<String>();
//		for (String a: allowed) {
//			demoAllowed.add(a);
//		}
	}


	@Override
	public ReadResourceResult apply(ReadResourceRequest t) {
		
		server.log(LoggingLevel.INFO, this, "apply( " + schema + ") ENTER");
		
		List<ResourceContents> contents = new ArrayList<ResourceContents>();
		JsonObject o = new JsonObject();

		try {
			PreparedStatement tables = server.getConnection().prepareStatement("SELECT NAME FROM SYSIBM.SYSTABLES WHERE CREATOR = ?;");
			tables.setString(1, schema);
			ResultSet tablesRs = tables.executeQuery();
			
			while (tablesRs.next()) {
				String tableName = tablesRs.getString(1);
				
				if (demoAllowed != null && !demoAllowed.contains(tableName.trim())) {
					continue;
				}

				PreparedStatement columns = server.getConnection().prepareStatement("SELECT NAME FROM SYSIBM.SYSCOLUMNS WHERE TBCREATOR = ? AND TBNAME = ?");
				columns.setString(1, schema);
				columns.setString(2, tableName);
				
				ResultSet columnsRs = columns.executeQuery();
				
				JsonArray array = new JsonArray();
				while (columnsRs.next()) {
					array.add(columnsRs.getString(1));
				}
				columnsRs.close();
				o.add(tableName, array);
				
			}
			tablesRs.close();
			
			contents.add(new TextResourceContents(
					url, 
					"application/json",
					o.toString()));
			
			server.log(LoggingLevel.INFO, this, "getColumns() EXIT");

		} catch (SQLException ex) {
			server.log(this, "SQLException information", ex);
		}

    	return new ReadResourceResult(contents);
	}
}
