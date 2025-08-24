package org.eclipse.mcp.builtin.resource.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mcp.Activator;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedResourceTemplateFactory;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedResourceTemplateFactory.ResourceTemplate;

/**
 * Contributes MCP Resource template "file://workspace/{relative-path}" and completion assistance
 */
@ResourceTemplate (
		uriTemplate = "file://workspace/{relative-path}",
		name = "Eclipse Workspace File",
		description = "Content of an file in an Eclipse workspace")
public class FileTemplates extends MCPAnnotatedResourceTemplateFactory {

	@Override
	public List<String> completionReq(String argumentName, String argumentValue, String uri, Map<String, String> arguments) {
		List<String> result = new ArrayList<String>();
		if (argumentName.equals("project")) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			for (IProject project: workspace.getRoot().getProjects()) {
				if (project.getName().toUpperCase().contains(argumentValue.toUpperCase())) {
					result.add(project.getName());
				}
			}
		} else if (argumentName.equals("name")) {
			
			String projectName = arguments.get("project");
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProject project =workspace.getRoot().getProject(projectName);
			if (project != null) {
				List<IFile> files = new ArrayList<IFile>();
				IResourceVisitor  visitor = new IResourceVisitor() {
					@Override
					public boolean visit(IResource resource) throws CoreException {
						if (resource instanceof IFile && resource.getName().contains(argumentValue)) {
							
							IFile file = (IFile)resource;
							if (file.isAccessible() && !file.isHidden() && file.exists() && !file.isPhantom()) {
								files.add(file);
							}
						}
						return files.size() < 50 && resource instanceof IContainer;
					}
				};
				try {
					project.accept(visitor, IResource.DEPTH_INFINITE, false);
				} catch (CoreException e) {
					e.printStackTrace();
				}
				
				for (IFile file: files) {
					result.add(file.getProjectRelativePath().toPortableString());
				}
			}
			
		}
		return result;
	}

	@Override
	public String[] readResource(String uri) {
		String result = Activator.getDefault().getResourceContent(uri);
		if (result != null) {
			return new String[] { result };
		}
		return null;
	}

}
