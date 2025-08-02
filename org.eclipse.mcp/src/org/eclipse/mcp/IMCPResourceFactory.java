package org.eclipse.mcp;

import java.util.UUID;

/**
 * Can be used by a <code>IMCPResourceController</code> to dynamically add or remove resources to the MCP server
 */
public interface IMCPResourceFactory {
	
	/**
	 * Add a resource to the server
	 * @param uri
	 * @param name
	 * @param description
	 * @param mimeType
	 * @return
	 */
	public UUID addResource(String uri, String name, String description, String mimeType);
	
	/**
	 * Remove a resource from the server
	 * @param uri
	 */
	public void removeResource(String uri);
}
