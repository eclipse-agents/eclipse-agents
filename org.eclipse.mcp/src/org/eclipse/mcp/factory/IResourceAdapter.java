package org.eclipse.mcp.factory;

import io.modelcontextprotocol.spec.McpSchema.ResourceLink;

/**
 * Supports the transformation between URIs, Eclipse objects, resource links and resource content
 * Supports the built-in tool readResource
 * Each template prefix must be unique or the adapter will be ignored
 * @param <T> the type of Eclipse object the adapter can transform URIs into
 */
public interface IResourceAdapter<T> {

	public IResourceAdapter<T> fromUri(String uri);
	
	public IResourceAdapter<T> fromModel(T object);
	
	public boolean supportsChildren();
	
	public IResourceAdapter<T>[] getChildren(int depth);
	
	public String getTemplate();
	
	public T getModel();
	
	public Object toJson();
	
	public ResourceLink toResourceLink();
	
	public String toUri();
	
	public String toContent();

}
