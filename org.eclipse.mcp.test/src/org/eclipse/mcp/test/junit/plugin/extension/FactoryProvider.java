package org.eclipse.mcp.test.junit.plugin.extension;

import org.eclipse.mcp.factory.IFactoryProvider;
import org.eclipse.mcp.factory.IResourceFactory;
import org.eclipse.mcp.factory.IResourceTemplateFactory;
import org.eclipse.mcp.factory.ToolFactory;

public class FactoryProvider implements IFactoryProvider {

	@Override
	public ToolFactory[] createToolFactories() {
		return AnnotatedToolFactory.createToolFactories(new Class<?>[] {
				AnnotatedToolFactory.class,
				AnnotatedToolFactoryComplex.class
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

}
