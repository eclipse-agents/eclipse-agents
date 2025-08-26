package org.eclipse.mcp.factory;

import org.eclipse.mcp.IMCPServices;

/**
 * Convenience for creating multiple factories programmatically
 */
public interface IFactoryProvider {

	public IResourceAdapter<?>[] createResourceAdapters();
	
	public Object[] getAnnotatedObjects();
	
	public abstract void initialize(IMCPServices services);

}
