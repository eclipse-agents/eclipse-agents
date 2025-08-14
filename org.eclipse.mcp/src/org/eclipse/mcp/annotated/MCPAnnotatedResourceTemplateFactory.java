package org.eclipse.mcp.annotated;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.mcp.IMCPResourceTemplateFactory;

import io.modelcontextprotocol.spec.McpSchema;



public abstract class MCPAnnotatedResourceTemplateFactory implements IMCPResourceTemplateFactory {
	
	
	List<ResourceTemplate> annotations = new ArrayList<ResourceTemplate>();
	List<McpSchema.ResourceTemplate> templates = new ArrayList<McpSchema.ResourceTemplate>(); 
	
	public MCPAnnotatedResourceTemplateFactory() {
		for (Annotation annotation: getClass().getAnnotations()) {
			if (annotation instanceof ResourceTemplates) {
				for (ResourceTemplate t: ((ResourceTemplates)annotation).value()) {
					annotations.add(t);
				}
				
			} else if (annotation instanceof ResourceTemplate) {
				annotations.add((ResourceTemplate)annotation);
			}
		}
		if (!annotations.isEmpty()) {
		
		} else {
			throw new IllegalArgumentException("MCPAnnotatedResourceTemplateFactory's empty constructor" + 
					" requires @ResourceTemplates or @ResourceTemplate annotation");
		}		
	}

	@Override
	public McpSchema.ResourceTemplate[] createResourceTemplates() {
		templates.clear();
		for (ResourceTemplate annotation: annotations) {
			
			McpSchema.Annotations templateAnnotation = null;
			if (annotation.roles().length > 0 || (
					annotation.priority() >= -1.0 && annotation.priority() < 1.0)) {
				templateAnnotation = new McpSchema.Annotations(Arrays.asList(annotation.roles()), annotation.priority());
			}
			templates.add(new McpSchema.ResourceTemplate(annotation.uriTemplate(), annotation.name(), 
					annotation.description(), annotation.mimeType(), templateAnnotation));
		}
		return templates.toArray(new McpSchema.ResourceTemplate[0]);
	}

	@Override
	public String getMimeType(String uri) {
		for (McpSchema.ResourceTemplate template: templates) {
			if (template.uriTemplate().equals(uri)) {
				return template.mimeType();
			}
		}
		return "plain/text";
	}

	
}