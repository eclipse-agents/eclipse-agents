package org.eclipse.mcp.resource;

/**
 * Supports the transformation between URIs, Eclipse objects, resource links and resource content
 * Supports the built-in tool readResource and getChildResources
 * 
 * @param <T> the type of Eclipse object the adapter can transform URIs into
 * @param <U> the type of JSON record the adapter can transform URIs into
 */
public interface IResourceAdapter<U> {

	public U toJson();

}
