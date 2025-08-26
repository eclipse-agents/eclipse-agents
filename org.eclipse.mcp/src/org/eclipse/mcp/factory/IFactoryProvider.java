package org.eclipse.mcp.factory;

/**
 * Convenience for creating multiple factories programmatically
 */
public interface IFactoryProvider extends IFactory {

	
	public IResourceFactory[] createResourceFactories();
	
	public IResourceAdapter<?>[] createResourceAdapters();
	
	public Object[] getAnnotatedObjects();
}
