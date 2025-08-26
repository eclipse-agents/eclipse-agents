package org.eclipse.mcp.builtin.resource.templates;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mcp.builtin.resource.EditorAdapter;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.springaicommunity.mcp.annotation.McpComplete;
import org.springaicommunity.mcp.annotation.McpResource;

public class Templates {

	public Templates() {
		
	}

    @McpResource(
    		uri = "eclipse://editor/{name}", 
            name = "Eclipse IDE Text Editor", 
            description = "Content of an Eclipse Text Editor")
	public String getEditorContent(String name) {		
		return new EditorAdapter().uriToResourceContent("eclipse://editor/" + name);
	}
    
    @McpComplete(uri="eclipse://editor/{name}")
	public List<String> completeName(String name) {
    	List<String> result = new ArrayList<String>();
		for (IWorkbenchWindow ww: PlatformUI.getWorkbench().getWorkbenchWindows()) {
			for (IWorkbenchPage page: ww.getPages()) {
				for (IEditorReference reference: page.getEditorReferences()) {
					result.add(reference.getName());
				}
			}
		}
    	return result;
	}
    
    @McpResource (
    		uri = "file://workspace/{relativePath}",
    		name = "Eclipse Workspace File",
    		description = "Content of an file in an Eclipse workspace")
    public String getWorkspaceFileContent(String relativePath) {		
    	return new EditorAdapter().uriToResourceContent("file://workspace/" + relativePath);
	}
    
    @McpComplete(uri="file://workspace/{relativePath}")
   	public List<String> completeRelativePath(String relativePath) {
   		return new ArrayList<String>();
   	}

}
