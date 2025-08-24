package org.eclipse.mcp.experimental.annotated;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
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


/**
 * Conveniences for creating multiple resource templates using annotations on a class extending MCPAnnotatedResourceTemplateFactory
 */
public abstract class MCPAnnotatedResourceTemplateFactory implements IResourceTemplateFactory {
	
	@Retention(RetentionPolicy.RUNTIME) // Match the retention of the repeatable annotation
	@Target(ElementType.TYPE)
	@Documented
	public @interface ResourceTemplates {
		ResourceTemplate[] value(); // Array of the repeatable annotation
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Repeatable(ResourceTemplates.class)
	@Documented
	public @interface ResourceTemplate {
		/**
		 * A URI template (according to RFC 6570) that can be used to construct resource URIs.
		 */
		String uriTemplate();
		/**
		 * Intended for programmatic or logical use, but used as a display name in past specs or fallback (if title isn't present).
		 */
		String name();
		/**
		 * A human-readable name for the type of resource this template refers to.
		 *
		 * This can be used by clients to populate UI elements.
		 */
		String title() default "";
		/**
		 * A description of what this template is for.
		 *
		 * This can be used by clients to improve the LLM's understanding of available resources. It can be thought of like a "hint" to the model.
		 */
		String description();
		/**
		 * The MIME type for all resources that match this template. This should only be included if all resources matching this template have the same type.
		 */
		String mimeType() default "text/plain";
		/**
		 * Describes who the intended customer of this object or data is.
		 *
		 * It can include multiple entries to indicate content useful for multiple audiences (e.g., `["user", "assistant"]`).
		 */
		McpSchema.Role[] roles() default { };
		/**
		 * Describes how important this data is for operating the server.
		 *
		 * A value of 1 means "most important," and indicates that the data is
		 * effectively required, while 0 means "least important," and indicates that
		 * the data is entirely optional.
		 *
		 * @minimum 0
		 * @maximum 1
		 */
		double priority() default -1;
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