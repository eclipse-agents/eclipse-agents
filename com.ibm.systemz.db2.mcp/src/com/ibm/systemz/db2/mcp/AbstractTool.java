package com.ibm.systemz.db2.mcp;



import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.ibm.systemz.db2.rse.db.queries.ResultSet;

public abstract class AbstractTool extends org.eclipse.mcp.AbstractTool {
	
	public AbstractTool() {
	}
	
	public abstract String getName();
	public abstract String getDescription();
	public abstract String getSchema();
	
	protected String[] runQuery(String connectionId, String sqlStatement) {
		Object response = ConnectionEnvironment.executeStatement(connectionId, sqlStatement);

		List<String> result = new ArrayList<String>();
		boolean isError = false;
		if (response instanceof Throwable) {
			throw (Throwable)response;
		} else if (response instanceof QueryModel) {
			//TODO needs hardening
			Execution execution = ((QueryModel)response).executions.get(0);
			ExecutionStatus executionStatus = execution.executionStatus;
			if ("-1".equals(executionStatus.returnCode)) {
				JsonObject o = new JsonObject();
				o.addProperty("returnCode", executionStatus.returnCode);
				o.addProperty("updateCount", executionStatus.updateCount);
				o.addProperty("sqlCode", executionStatus.sqlCode);
				o.addProperty("sqlState", executionStatus.sqlState);
				o.addProperty("messageText", executionStatus.messageText);
				o.addProperty("elapsedTime", executionStatus.elapsedTime);
				result.add(o.toString());
				isError = true;
			} else {
				ResultSet resultSet = execution.resultSets[0];
				for (String[] row: resultSet.getRows()) {
					JsonObject o = new JsonObject();
					for (int i = 0; i < resultSet.colCount; i++) {
						o.addProperty(resultSet.columnNames[i], row[i]);
					}
					result.add(new TextContent(o.toString()));
				}
				return new CallToolResult(result, false);
			}
		} else {
			//TODO
			result.add(new TextContent("Unexpected Response"));
	}
	
	return new CallToolResult(result, false);
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
