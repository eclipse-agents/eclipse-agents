package org.eclipse.mcp.builtins.json;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonClassDescription("List of file and/or folder resources in the Eclipse workspace")
public class Resources {
	
	public Resources(IContainer container, int depth) {
		List<Resource> children = new ArrayList<Resource>();
		
		depth = Math.max(0, Math.min(2, depth));

		try {
			for (IResource child: container.members()) {
				child.accept(new IResourceVisitor() {
					@Override
					public boolean visit(IResource child) throws CoreException {
						if (child != container) {
							children.add(new Resource(child));
						}
						return true;
					}
				}, depth, false);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		resources = children.toArray(Resource[]::new);
	}

	@JsonProperty
	public Resource[] resources;
}
