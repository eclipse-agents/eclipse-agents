package org.eclipse.mcp;

import java.util.UUID;

public interface IMCPResourceManager {
	
	public UUID addResource(String uri, String name, String description, String mimeType);
	
	public void removeResource(String uri);
}
