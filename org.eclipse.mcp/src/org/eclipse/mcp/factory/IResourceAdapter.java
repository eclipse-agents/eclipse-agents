package org.eclipse.mcp.factory;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.ResourceLink;

/**
 * Supports the transformation between URIs, Eclipse objects, resource links and resource content
 * Supports the built-in tool readResource
 * Each template prefix must be unique or the adapter will be ignored
 * @param <T> the type of Eclipse object the adapter can transform URIs into
 */
public interface IResourceAdapter<T> extends IFactory {

	public String getTemplate();
	
	public String getUniqueTemplatePrefix();
	
	public T uriToEclipseObject(String uri);
	
	public Object eclipseObjectToJsonObject(T object);
	
	public ResourceLink eclipseObjectToResourceLink(T object);
	
	public String eclipseObjectToURI(T object);
	
	public String eclipseObjectToResourceContent(T object);
	
	public default String uriToResourceContent(String uri) {
		T object =  uriToEclipseObject(uri);
		if (object != null) {
			return eclipseObjectToResourceContent(object);
		}
		return null;
	}
	
	public default void addAnnotations(McpSchema.ResourceLink.Builder builder) {
		//TODO if (preference)
		
//		builder.annotations(new Annotations(Arrays.asList(Role.ASSISTANT, Role.USER), 1.0));
	}
}
