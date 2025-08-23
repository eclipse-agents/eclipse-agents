package org.eclipse.mcp.builtin.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.Annotations;
import io.modelcontextprotocol.spec.McpSchema.ResourceLink;
import io.modelcontextprotocol.spec.McpSchema.Role;

public class RelativeFileAdapter implements IResourceAdapter<IResource> {
	
	final String template = "file://workspace/{relative-path}";
	boolean includeAnnotations = false;

	@Override
	public String getTemplate() {
		return template;
	}

	@Override
	public String getUniqueTemplatePrefix() {
		return template.substring(0, template.indexOf("{"));
	}

	@Override
	public IResource uriToEclipseObject(String uri) {
		IResource resource = null;
		if (uri.equals(getUniqueTemplatePrefix())) {
			return ResourcesPlugin.getWorkspace().getRoot();
		} else if (uri.startsWith(getUniqueTemplatePrefix())) {
			String relativePath = uri.substring(getUniqueTemplatePrefix().length());
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			resource = workspace.getRoot().findMember(relativePath);
			
			if (resource == null) {
				relativePath = URLDecoder.decode(relativePath);
				resource = workspace.getRoot().findMember(relativePath);
			}
		}
		return resource;
	}

	@Override
	public Object eclipseObjectToJsonObject(IResource object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceLink eclipseObjectToResourceLink(IResource resource) {

		McpSchema.ResourceLink.Builder builder =  McpSchema.ResourceLink.builder()
				.uri(eclipseObjectToURI(resource))
				.name(resource.getName());
		
		addAnnotations(builder);
				
		
		if (resource instanceof IFile) {
			builder.description("Text content of file in Eclipse workspace");
			builder.mimeType("text/plain");

			try {
				IFileStore store = EFS.getStore(resource.getLocationURI());
				IFileInfo info = store.fetchInfo();
				builder.size(info.getLength());
				
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} else {
			
		}

		return builder.build();
	}

	@Override
	public String eclipseObjectToURI(IResource resource) {
		return getUniqueTemplatePrefix() + resource.getFullPath().toPortableString().substring(1);
	}

	@Override
	public String eclipseObjectToResourceContent(IResource resource) {
		String content = null;
		if (resource instanceof IFile) {
			try {
				InputStreamReader reader = new InputStreamReader(((IFile)resource).getContents());
				BufferedReader breader = new BufferedReader(reader);
				content = breader.lines().collect(Collectors.joining("\n")); //$NON-NLS-1$
				breader.close();
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}
}
