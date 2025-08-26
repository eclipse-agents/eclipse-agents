package org.eclipse.mcp.builtins;

import org.eclipse.mcp.builtin.resource.AbsoluteFileAdapter;
import org.eclipse.mcp.builtin.resource.ConsoleAdapter;
import org.eclipse.mcp.builtin.resource.EditorAdapter;
import org.eclipse.mcp.builtin.resource.RelativeFileAdapter;
import org.eclipse.mcp.builtin.resource.factory.Editors;
import org.eclipse.mcp.builtin.resource.templates.Templates;
import org.eclipse.mcp.builtins.tools.BuiltinAnnotatedToolsFactory;
import org.eclipse.mcp.factory.IFactoryProvider;
import org.eclipse.mcp.factory.IResourceAdapter;
import org.eclipse.mcp.factory.IResourceFactory;

public class BuiltinFactoryProvider implements IFactoryProvider {


	@Override
	public IResourceFactory[] createResourceFactories() {
		return  new IResourceFactory[] {
				new Editors()
		};
	}


	@Override
	public IResourceAdapter<?>[] createResourceAdapters() {
		return new IResourceAdapter[] {
			new ConsoleAdapter(),
			new EditorAdapter(),
			new RelativeFileAdapter(),
			new AbsoluteFileAdapter()
		};
	}

	@Override
	public Object[] getAnnotatedObjects() {
		return new Object[] {
			new BuiltinAnnotatedToolsFactory(),
			new Templates()
		};
	}

}
