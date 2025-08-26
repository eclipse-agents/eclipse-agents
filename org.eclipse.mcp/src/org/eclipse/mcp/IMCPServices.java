package org.eclipse.mcp;

import io.modelcontextprotocol.server.McpServerFeatures.SyncResourceSpecification;

public interface IMCPServices {

	/**
	 * Adds a resource to the server.  
	 * Can be used to dynamically make resources available based on state of IDE.
	 * For example, to dynamically have a resource for each open editor available
	 * @param uri
	 * @param name
	 * @param description
	 * @param mimeType
	 * @return
	 */
	public void addResource(SyncResourceSpecification resource);
	
	/**
	 * Removes a previously added resource from the server
	 * @param uri
	 */
	public void removeResource(String uri);
	
	/**
	 * @param toolName
	 * @return true if toolName is currently available on server
	 */
	public boolean getToolVisibility(String toolName);
	
	/**
	 * Adds / Removes a tool from the server
	 * Can be used to remove declared tools from the server based on user preferences
	 * @param toolName
	 * @param isVisible
	 * @return true only if the tool availability was changed
	 */
	public boolean setToolVisibility(String toolName, boolean isVisible);
}
