package org.eclipse.mcp.builtins.json;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonClassDescription("List of file and/or folder resources in the Eclipse workspace")
public class Resources {
	
	public Resources() {
		
	}

	public Resources(IContainer container) {
		try {
			IResource[] children = container.members();
			resources = new Resource[children.length];
			for (int i = 0; i < children.length; i++) {
				resources[i] = new Resource(children[i], 0);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@JsonProperty
	public Resource[] resources;
}
