package org.eclipse.mcp.platform;

import org.eclipse.mcp.IFactoryProvider;
import org.eclipse.mcp.IMCPServices;
import org.eclipse.mcp.platform.resource.ConsoleAdapter;
import org.eclipse.mcp.platform.resource.EditorAdapter;
import org.eclipse.mcp.platform.resource.RelativeFileAdapter;
import org.eclipse.mcp.resource.IResourceTemplate;

public class FactoryProvider implements IFactoryProvider {

	ResourceController editors;
	
	public FactoryProvider() {
		editors = new ResourceController();
	}


	@Override
	public IResourceTemplate<?, ?>[] createResourceTemplates() {
		return new IResourceTemplate[] {
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
