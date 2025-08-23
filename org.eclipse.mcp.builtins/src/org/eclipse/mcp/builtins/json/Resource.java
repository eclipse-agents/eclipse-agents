package org.eclipse.mcp.builtins.json;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
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
	
	public Resource(IResource resource) {
		this.name = resource.getName();
		RelativeFileAdapter ra = new RelativeFileAdapter();
		this.workspace_uri = ra.eclipseObjectToResourceLink(resource);
		
		if (resource instanceof IFile) {
			isFolder = false;
		} else if (resource instanceof IContainer) {
			isFolder = true;
		}
	}
}
