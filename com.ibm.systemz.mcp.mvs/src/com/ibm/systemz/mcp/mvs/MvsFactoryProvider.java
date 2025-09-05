package com.ibm.systemz.mcp.mvs;

import org.eclipse.mcp.IFactoryProvider;
import org.eclipse.mcp.IMCPServices;
import org.eclipse.mcp.resource.IResourceTemplate;

public class MvsFactoryProvider implements IFactoryProvider {

	@Override
	public IResourceTemplate<?, ?>[] createResourceTemplates() {
		return new IResourceTemplate[] { new MvsResourceAdapter() };
	}

	@Override
	public Object[] getAnnotatedObjects() {
		return new Object[] {
				new Templates()
		};
	}

	@Override
	public void initialize(IMCPServices services) {
	}
}
