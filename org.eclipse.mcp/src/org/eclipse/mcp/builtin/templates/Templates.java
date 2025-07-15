package org.eclipse.mcp.builtin.templates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.modelcontextprotocol.spec.McpSchema;

public class Templates  {

	public List<McpSchema.ResourceTemplate> templates = new ArrayList<McpSchema.ResourceTemplate>();
	
	public Templates() {
		
		McpSchema.Annotations annotations = new McpSchema.Annotations(Arrays.asList(McpSchema.Role.USER), 0.5);

		templates.add(new McpSchema.ResourceTemplate("resource://editor/{filename}/content", 
				"Editor Content",
				"Get the text of an open Eclipse editor", 
				"text/plain", annotations));
		
		templates.add(new McpSchema.ResourceTemplate("resource://editor/{filename}/annotations", 
				"Editor Content",
				"Get the text of an open Eclipse editor", 
				"text/plain", annotations));

		
		
	}
}
