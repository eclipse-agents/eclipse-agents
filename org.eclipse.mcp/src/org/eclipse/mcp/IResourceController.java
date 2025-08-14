package org.eclipse.mcp;

import io.modelcontextprotocol.spec.McpSchema.Resource;

public interface IResourceController {

	/**
	 * Add a resource to the server
	 * @param uri
	 * @param name
	 * @param description
	 * @param mimeType
	 * @return
	 */
	public void addResource(Resource resource);
	
	/**
	 * Remove a resource from the server
	 * @param uri
	 */
	public void removeResource(String uri);
}
