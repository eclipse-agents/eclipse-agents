package org.eclipse.mcp;

public interface IMCPResourceManagerFactory {
	
	public void initialize(IMCPResourceManager manager);
	
	public String[] readResource(String url);
}
