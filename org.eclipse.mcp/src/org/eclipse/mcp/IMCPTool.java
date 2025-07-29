package org.eclipse.mcp;

import java.util.Map;

public interface IMCPTool {
	
	/** 
	 * @param The tools input parameters as defined by its declared JSON Schema
	 * @return An array of strings representing the result of the tool execution.
	 */
	public String[] apply(Map<String, Object> args);
}
