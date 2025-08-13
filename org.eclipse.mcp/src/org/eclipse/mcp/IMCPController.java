package org.eclipse.mcp;

import java.util.UUID;

import io.modelcontextprotocol.spec.McpSchema.Resource;

public interface IMCPController {

	/**
	 * Add a resource to the server
	 * @param uri
	 * @param name
	 * @param description
	 * @param mimeType
	 * @return
	 */
	public UUID addResource(Resource resource);
	
	/**
	 * Remove a resource from the server
	 * @param uri
	 */
	public void removeResource(String uri);
}
