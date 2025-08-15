package org.eclipse.mcp.factory;

/**
 * Convenience for creating multiple factories programmatically
 */
public interface IFactoryProvider extends IFactory {

	public ToolFactory[] createToolFactories();
	
	public IResourceFactory[] createResourceFactories();
	
	public IResourceTemplateFactory[] createResourceTemplateFactories();
}
