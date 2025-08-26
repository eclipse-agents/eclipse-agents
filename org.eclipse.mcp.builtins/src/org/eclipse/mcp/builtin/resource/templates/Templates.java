package org.eclipse.mcp.builtin.resource.templates;

import java.util.ArrayList;
import java.util.List;

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
		return "hello";
	}
    
    @McpComplete(uri="eclipse://editor/{name}")
	public List<String> completeName(String name) {
    	return new ArrayList<String>();
	}
    
    @McpResource (
    		uri = "file://workspace/{relativePath}",
    		name = "Eclipse Workspace File",
    		description = "Content of an file in an Eclipse workspace")
    public String getWorkspaceFileContent(String relativePath) {		
		return "hello";
	}
    
    @McpComplete(uri="file://workspace/{relativePath}")
   	public List<String> completeRelativePath(String relativePath) {
   		return new ArrayList<String>();
   	}

}
