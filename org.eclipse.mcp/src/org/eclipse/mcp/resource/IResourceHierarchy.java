package org.eclipse.mcp.resource;

import org.eclipse.mcp.platform.resource.ResourceSchema.Children;
import org.eclipse.mcp.platform.resource.ResourceSchema.DEPTH;

public interface IResourceHierarchy<T, U> extends IResourceTemplate<T, U> {

	public Children<U> getChildren(DEPTH depth);
}
