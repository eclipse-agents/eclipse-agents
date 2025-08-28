package org.eclipse.mcp.builtins;

import org.eclipse.mcp.IMCPServices;
import org.eclipse.mcp.builtin.resource.ConsoleAdapter;
import org.eclipse.mcp.builtin.resource.EditorAdapter;
import org.eclipse.mcp.builtin.resource.RelativeFileAdapter;
import org.eclipse.mcp.builtin.resource.factory.Editors;
import org.eclipse.mcp.builtin.resource.templates.Templates;
import org.eclipse.mcp.builtins.tools.BuiltinAnnotatedToolsFactory;
import org.eclipse.mcp.factory.IFactoryProvider;
import org.eclipse.mcp.factory.IResourceAdapter;

public class FactoryProvider implements IFactoryProvider {

	Editors editors;
	
	public FactoryProvider() {
		editors = new Editors();
	}


	@Override
	public IResourceAdapter<?, ?>[] createResourceAdapters() {
		return new IResourceAdapter[] {
			new ConsoleAdapter(),
			new EditorAdapter(),
			new RelativeFileAdapter()
//			new AbsoluteFileAdapter()
		};
	}

	@Override
	public Object[] getAnnotatedObjects() {
		return new Object[] {
			new BuiltinAnnotatedToolsFactory(),
			new Templates()
		};
	}


	@Override
	public void initialize(IMCPServices services) {
		editors.initialize(services);
	}

}
