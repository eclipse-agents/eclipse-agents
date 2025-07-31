package com.ibm.systemz.db2.mcp.tools;

import java.util.Map;

import org.eclipse.mcp.IMCPTool;

import com.google.gson.Gson;

/**
{
  "type": "object",
  "properties": {
    "query": {
      "type": "string"
    }
  },
  "required": ["query"]
}
 */

public class RunQuery implements IMCPTool {

		Gson gson;
		public RunQuery() {
			super();
			gson = new Gson();
		}
		

		@Override
		public String[] apply(Map<String, Object> u) {
			return new String[0];
		}
	}
