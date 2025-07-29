package org.eclipse.mcp;

public interface IMCPResourceFactory {
	
	public void initialize(IMCPResourceManager manager);
	
	public String[] readResource(String url);
}
