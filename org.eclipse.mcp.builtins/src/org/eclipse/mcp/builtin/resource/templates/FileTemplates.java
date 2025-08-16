package org.eclipse.mcp.builtin.resource.templates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedResourceTemplateFactory;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedResourceTemplateFactory.ResourceTemplate;

@ResourceTemplate (
		uriTemplate = "eclipse:///file/{project}/{name}",
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
	public String[] readResource(String url) {
		String prefix = "eclipse:///file/";
		String postfix = url.substring(prefix.length());
		String projectName = postfix.split("/")[0];
		String fileRelativePath = postfix.substring(projectName.length() + 1);
		
		IPath path = Path.fromPortableString(fileRelativePath);
		path.toOSString();
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(projectName);
		if (project != null) {
			IResource resource = project.findMember(fileRelativePath);
			
			if (resource == null) {
				fileRelativePath = URLDecoder.decode(fileRelativePath);
				resource = project.findMember(fileRelativePath);
			}
			if (resource instanceof IFile) {
				IFile file = (IFile)resource;
				
				try (InputStreamReader reader = new InputStreamReader(
						file.getContents(), file.getCharset())) {
				       
					BufferedReader breader = new BufferedReader(reader);
					String read = breader.lines().collect(Collectors.joining("\n")); //$NON-NLS-1$
					if (read != null) {
						return new String[] { read };
					}
				}catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (CoreException e) {
					e.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
		}
	
		return new String[0];
	}

}
