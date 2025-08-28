package com.ibm.systemz.mcp.mvs;

import org.eclipse.mcp.IMCPServices;
import org.eclipse.mcp.factory.IFactoryProvider;
import org.eclipse.mcp.factory.IResourceAdapter;

public class MvsFactoryProvider implements IFactoryProvider {

	@Override
	public IResourceAdapter<?>[] createResourceAdapters() {
		// TODO Auto-generated method stub
		return new IResourceAdapter[] { new MvsResourceAdapter() };
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
