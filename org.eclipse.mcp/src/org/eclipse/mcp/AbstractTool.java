package org.eclipse.mcp;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonObject;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.Tool;

public abstract class AbstractTool implements Function<Map<String, Object>, CallToolResult> {
	
	protected Server server;
	
	public AbstractTool(Server server) {
		this.server = server;
		Tool tool = new Tool(getName(), getDescription(), getSchema());
		server.getSyncServer().addTool(new SyncToolSpecification(tool, this));
	}
	
	public abstract String getName();
	public abstract String getDescription();
	public abstract String getSchema();
	
	public CallToolResult toResult(ResultSet rs) throws SQLException {
		List<Content> result = new ArrayList<Content>();
		
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();

		while (rs.next()) {
			JsonObject o = new JsonObject();
				
			for (int i = 1; i <= columnCount; i++) {
				o.addProperty(rsmd.getColumnLabel(i), rs.getString(i));
			}
				
			result.add(new TextContent(o.toString()));
		}
		
		return new CallToolResult(result, false);
	}
	
	public CallToolResult toResult(Exception e) {
		List<Content> result = new ArrayList<Content>();
		result.add(new TextContent(e.getLocalizedMessage()));
		return new CallToolResult(result, true);
	}
}
