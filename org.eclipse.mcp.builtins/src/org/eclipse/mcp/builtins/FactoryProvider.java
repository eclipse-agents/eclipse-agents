package org.eclipse.mcp.builtins;

import org.eclipse.mcp.IFactoryProvider;
import org.eclipse.mcp.IMCPServices;
import org.eclipse.mcp.IResourceAdapter;
import org.eclipse.mcp.builtin.resourceadapters.ConsoleAdapter;
import org.eclipse.mcp.builtin.resourceadapters.EditorAdapter;
import org.eclipse.mcp.builtin.resourceadapters.RelativeFileAdapter;

public class FactoryProvider implements IFactoryProvider {

	ResourceController editors;
	
	public FactoryProvider() {
		editors = new ResourceController();
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
			new Tools(),
			new ResaourceTemplates()
		};
	}


	@Override
	public void initialize(IMCPServices services) {
		editors.initialize(services);
	}

}
