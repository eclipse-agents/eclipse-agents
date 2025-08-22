package org.eclipse.mcp.builtin.resource;

import io.modelcontextprotocol.spec.McpSchema.ResourceLink;

public interface IResourceAdapter<T> {

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
}
