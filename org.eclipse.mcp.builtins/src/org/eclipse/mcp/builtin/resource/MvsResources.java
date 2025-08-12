package org.eclipse.mcp.builtin.resource;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mcp.IMCPResourceController;
import org.eclipse.mcp.IMCPResourceFactory;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;

public class MvsResources implements IMCPResourceController {

	IMCPResourceFactory manager;
	
	IWindowListener windowListener;
	IPageListener pageListener;
	IPartListener partListener;
	
	Set<String> openURIs = new HashSet<String>();

	public MvsResources() {
		super();
	}

	@Override
	public void initialize(IMCPResourceFactory manager) {
		this.manager = manager;
	}

	@Override
	public String[] readResource(String url) {
		// TODO Auto-generated method stub
		return null;
	}
}
