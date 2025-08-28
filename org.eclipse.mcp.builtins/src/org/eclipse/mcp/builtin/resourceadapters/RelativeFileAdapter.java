package org.eclipse.mcp.builtin.resourceadapters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mcp.IResourceAdapter;
import org.eclipse.mcp.MCPException;
import org.eclipse.mcp.Schema.DEPTH;
import org.eclipse.mcp.Schema.File;
import org.eclipse.mcp.Schema.Files;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.ResourceLink;
import io.modelcontextprotocol.util.DefaultMcpUriTemplateManager;

/**
 * support for resource template: file://workspace/{relativePath}
 */
public class RelativeFileAdapter implements IResourceAdapter<IResource, File> {
	
	final String template = "file://workspace/{relativePath}";
	final String prefix = template.substring(0, template.indexOf("{"));
	IResource resource;
	
	public RelativeFileAdapter() {}

	public RelativeFileAdapter(IResource resource) {
		this.resource = resource;
	}
	
	public RelativeFileAdapter(String uri) {
		DefaultMcpUriTemplateManager tm = new DefaultMcpUriTemplateManager(template);
		if (tm.matches(uri)) {
			Map<String, String> variables = tm.extractVariableValues(uri);
			String relativePath = variables.get("relativePath");
			
			if (relativePath == null || relativePath.isBlank()) {
				resource = ResourcesPlugin.getWorkspace().getRoot();
			} else {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				resource = workspace.getRoot().findMember(relativePath);
				
				if (resource == null) {
					relativePath = URLDecoder.decode(relativePath, StandardCharsets.UTF_8);
					resource = workspace.getRoot().findMember(relativePath);
				}
			}
		}
		
		if (resource == null) {
			throw new MCPException("uri not resolved: " + uri);
		}
	}

	@Override
	public String[] getTemplates() {
		return new String[] { template };
	}
	
	@Override
	public RelativeFileAdapter fromUri(String uri) {
		return new RelativeFileAdapter(uri);
	}

	@Override
	public RelativeFileAdapter fromModel(IResource console) {
		return new RelativeFileAdapter(console);
	}

	@Override
	public boolean supportsChildren() {
		return resource instanceof IContainer;
	}

	@Override
	public Files getChildren(DEPTH depth) {
		
		List<File> children = new ArrayList<File>();
		if (depth == null) {
			depth = DEPTH.CHILDREN;
		}
		
		if (resource instanceof IContainer) {
			try {
				for (IResource child: ((IContainer)resource).members()) {
					child.accept(new IResourceVisitor() {
						@Override
						public boolean visit(IResource child) throws CoreException {
							if (child != resource) {
								children.add(new RelativeFileAdapter(child).toJson());
							}
							return true;
						}
					}, depth.value(), false);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		
		return new Files(children.toArray(File[]::new), depth);
	}

	@Override
	public IResource getModel() {
		return resource;
	}

	@Override
	public File toJson() {
		return new File(resource.getName(), resource instanceof IContainer, toResourceLink());
	}

	@Override
	public ResourceLink toResourceLink() {
		McpSchema.ResourceLink.Builder builder =  McpSchema.ResourceLink.builder()
				.uri(toUri())
				.name(resource.getName());
				
		if (resource instanceof IFile) {
			builder.description("Eclipse workspace file");
			builder.mimeType("text/plain");

			try {
				IFileStore store = EFS.getStore(resource.getLocationURI());
				IFileInfo info = store.fetchInfo();
				builder.size(info.getLength());
				
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} else if (resource instanceof IProject) {
			builder.description("Eclipse workspace project");
		} else if (resource instanceof IWorkspaceRoot) {
			builder.description("Eclipse workspace root");
		} else if (resource instanceof IFolder) {
			builder.description("Eclipse workspace folder");
		}

		return builder.build();
		
	}

	@Override
	public String toUri() {
		return prefix + URLEncoder.encode( resource.getFullPath().toPortableString().substring(1), StandardCharsets.UTF_8);
	}

	@Override
	public String toContent() {
		
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
