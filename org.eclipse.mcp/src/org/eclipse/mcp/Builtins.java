package org.eclipse.mcp;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mcp.builtin.resource.Editors;
import org.eclipse.mcp.builtin.templates.Templates;
import org.eclipse.mcp.builtin.tool.ListConsoles;
import org.eclipse.mcp.builtin.tool.Problems;

public class Builtins {

	public List<AbstractTool> tools = new ArrayList<AbstractTool>();
	public List<AbstractResource> resources = new ArrayList<AbstractResource>();
	Templates templates;

	public Builtins() {
		
		tools.add(new Problems());
		tools.add(new ListConsoles());
		
		// Resources
		resources.add(new Editors());
//		new Schema(this, "ADMF001");
//		new Schema(this, "SYSIBM");
		
		templates = new Templates();
		
	}

}
