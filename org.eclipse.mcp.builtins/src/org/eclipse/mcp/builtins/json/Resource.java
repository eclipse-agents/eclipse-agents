package org.eclipse.mcp.builtins.json;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mcp.builtin.resource.RelativeFileAdapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.modelcontextprotocol.spec.McpSchema;

public class Resource {
	
	String name;
	boolean isFolder;

	@JsonProperty
	@JsonPropertyDescription("Relative path for resource within Eclipse workspace")
	McpSchema.ResourceLink workspace_uri;
	
	
	public Resource(IResource resource, int depth) {
		this.name = resource.getName();
		RelativeFileAdapter ra = new RelativeFileAdapter();
		this.workspace_uri = ra.eclipseObjectToResourceLink(resource);
		
		if (resource instanceof IFile) {
			isFolder = false;
		} else if (resource instanceof IContainer) {
			isFolder = true;
//			if (depth > 0) {
//				List<Resource> childrenList = new ArrayList<Resource>();
//				try {
//					for (IResource child: ((IContainer)resource).members()) {
//						childrenList.add(new Resource(child, depth - 1));
//					}
//				} catch (CoreException e) {
//					e.printStackTrace();
//				}
//				children = new Resources();
//				children.resources = childrenList.toArray(Resource[]::new);
//			}
		}
	}
}
