package org.eclipse.mcp;

/**
 * Convenience for creating multiple factories programmatically
 */
public interface IFactoryProvider {

	/**
	 * Adapters supporting passthrough between tools and resource templates.
	 * Enables the platform tools "getResourceChildren" and "readResource" to
	 * resolve URIs to json and resource content
	 * @return
	 */
	public IResourceAdapter<?, ?>[] createResourceAdapters();
	
	/**
	 * Return objects annotated with annotations from "mcp-annotations'
	 * @return
	 */
	public Object[] getAnnotatedObjects();
	
	/**
	 * Called on server start and restart, use it to populate the initial conditions
	 * for resource additions and tool visibility
	 * @param services
	 */
	public abstract void initialize(IMCPServices services);

}
