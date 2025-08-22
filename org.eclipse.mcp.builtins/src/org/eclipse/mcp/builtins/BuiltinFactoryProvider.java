package org.eclipse.mcp.builtins;

import org.eclipse.mcp.builtin.resource.factory.Editors;
import org.eclipse.mcp.builtin.resource.templates.EditorTemplates;
import org.eclipse.mcp.builtin.resource.templates.FileTemplates;
import org.eclipse.mcp.builtins.tools.BuiltinAnnotatedToolsFactory;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedToolFactory;
import org.eclipse.mcp.factory.IFactoryProvider;
import org.eclipse.mcp.factory.IResourceFactory;
import org.eclipse.mcp.factory.IResourceTemplateFactory;
import org.eclipse.mcp.factory.ToolFactory;

public class BuiltinFactoryProvider implements IFactoryProvider {

	@Override
	public ToolFactory[] createToolFactories() {
		return MCPAnnotatedToolFactory.createToolFactories(BuiltinAnnotatedToolsFactory.class);
	}

	@Override
	public IResourceFactory[] createResourceFactories() {
		return  new IResourceFactory[] {
				new Editors()
		};
	}

	@Override
	public IResourceTemplateFactory[] createResourceTemplateFactories() {
		return new IResourceTemplateFactory[] {
				new EditorTemplates(),
				new FileTemplates()
		};
	}

}
