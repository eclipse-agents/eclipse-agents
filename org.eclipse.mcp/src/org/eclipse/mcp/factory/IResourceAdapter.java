package org.eclipse.mcp.factory;

import org.eclipse.mcp.Schema.Files;

import io.modelcontextprotocol.spec.McpSchema.ResourceLink;

/**
 * Supports the transformation between URIs, Eclipse objects, resource links and resource content
 * Supports the built-in tool readResource
 * Each template prefix must be unique or the adapter will be ignored
 * @param <T> the type of Eclipse object the adapter can transform URIs into
 */
public interface IResourceAdapter<T, U> {

	public IResourceAdapter<T, U> fromUri(String uri);
	
	public IResourceAdapter<T, U> fromModel(T object);
	
	public boolean supportsChildren();
	
	public Files getChildren(int depth);
	
	public String getTemplate();
	
	public T getModel();
	
	public U toJson();
	
	public ResourceLink toResourceLink();
	
	public String toUri();
	
	public String toContent();

}
