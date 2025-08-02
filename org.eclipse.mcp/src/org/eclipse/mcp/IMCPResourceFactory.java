package org.eclipse.mcp;

/**
 * An MCP resource represents content an LLM agent or user can opt to add to their context window.
 * 
 * MCP resources can be dynamically added/removed to the list of resources available on a server by a <code>resourceFactory</code>
 *
 * Resource factories are declared in extension point <code>org.eclipse.mcp.modelContextProtocolServer</code>
 * 
 * The <code>class<code> attribute of a <code>resourceFactory</code> element must be an instance of <code>org.eclipse.mcp.IMCPResourceFactory</code>
 */
public interface IMCPResourceFactory {
	
	/**
	 * 
	 * @param manager The manager can be used by your factory to dynamically add and remove resources to its MCP server
	 */
	public void initialize(IMCPResourceManager manager);
	
	/**
	 * Used to lazily resolve a url to content
	 * @param url
	 * @return the resolved content of the url
	 */
	public String[] readResource(String url);
}
