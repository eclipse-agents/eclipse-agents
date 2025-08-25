package com.ibm.systemz.mcp.mvs;

import org.eclipse.mcp.factory.IFactoryProvider;
import org.eclipse.mcp.factory.IResourceAdapter;
import org.eclipse.mcp.factory.IResourceFactory;
import org.eclipse.mcp.factory.IResourceTemplateFactory;
import org.eclipse.mcp.factory.ToolFactory;

public class MvsFactoryProvider implements IFactoryProvider {

	@Override
	public ToolFactory[] createToolFactories() {
		return AnnotatedToolFactory.createToolFactories(new Class<?>[] {
			AnnotatedToolFactory.class
	});
}

	@Override
	public IResourceFactory[] createResourceFactories() {
		return new IResourceFactory[] {
			new ResourceFactory()
		};
	}
	
	@Override
	public IResourceTemplateFactory[] createResourceTemplateFactories() {
		return new IResourceTemplateFactory[] {
			new AnnotatedResourceTemplateFactory()
		};
	}
	
	@Override
	public IResourceAdapter<?>[] createResourceAdapters() {
		return new IResourceAdapter[] {
			new MvsResourceAdapter()
		};
	}

}
