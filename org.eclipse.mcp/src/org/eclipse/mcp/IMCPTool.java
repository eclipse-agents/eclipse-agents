package org.eclipse.mcp;

import java.util.Map;

/**
 * An MCP tool is an exported function that and LLM-powered Agent can invoke
 * 
 * MCP Tools are declared in extension point <code>org.eclipse.mcp.modelContextProtocolServer</code>
 * 
 * The <code>class<code> attribute of a <code>tool</code> must be an instance of <code>org.eclipse.mcp.IMCPTool</code>
 */
public interface IMCPTool {
	
	/**
	 * Executes the MCP Tool
	 * @param args A map of input parameters sent to this function matching the tool's declared input JSON schema
	 * @param properties utility to fetch and prompt for user customized preference values
	 * @return An array of strings representing the result of the tool execution.
	 */
	public String[] apply(Map<String, Object> args, IElementProperties properties);

}
