package com.ibm.systemz.db2.mcp.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.mcp.IMCPTool;

import com.google.gson.Gson;
import com.ibm.systemz.db2.ide.ConnectionEnvironment;
import com.ibm.systemz.db2.ide.ConnectionSummary;
import com.ibm.systemz.db2.ide.ConnectionSummary.KIND;
import com.ibm.systemz.db2.rse.db.model.LocationGeneralModel;

/**
{
  "type": "object",
  "properties": {},
  "required": []
}
 */
public class ListConnections implements IMCPTool {

	Gson gson;
	public ListConnections() {
		super();
		gson = new Gson();
	}
	

	@Override
	public String[] apply(Map<String, Object> u) {
		
		List<Db2ZosConnection> result = new ArrayList<Db2ZosConnection>();
		for (ConnectionSummary summary: ConnectionEnvironment.getLocationSummaries()) {
			if (summary.getKind() == KIND.db2) {
				LocationGeneralModel model = new LocationGeneralModel(summary.getDb2SubSystem(), summary.getId());
				result.add(new Db2ZosConnection(summary.getName(), summary.getId().toString(), model.getDerrivedHost(), model.getLocation()));
			}
		}
		
		return result.stream().map(conn -> {
			return gson.toJson(conn);
		}).toArray(String[]::new);
	}
	
	class Db2ZosConnection {
		String name;
		String host;
		String location;

		public Db2ZosConnection(String name, String id,  String host, String location) {
			super();
			this.name = name;
			this.host = host;
			this.location = location;
		}
	}
}
