package org.eclipse.mcp.experimental.annotated;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.mcp.factory.IResourceTemplateFactory;

import io.modelcontextprotocol.spec.McpSchema;



public abstract class MCPAnnotatedResourceTemplateFactory implements IResourceTemplateFactory {
	
	@Retention(RetentionPolicy.RUNTIME) // Match the retention of the repeatable annotation
	@Target(ElementType.TYPE)
	public @interface ResourceTemplates {
		ResourceTemplate[] value(); // Array of the repeatable annotation
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Repeatable(ResourceTemplates.class)
	public @interface ResourceTemplate {
		String uriTemplate();
		String name();
		String title() default "";
		String description();
		String mimeType() default "text/plain";
		McpSchema.Role[] roles() default { };
		double priority() default -10;
	}
	
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