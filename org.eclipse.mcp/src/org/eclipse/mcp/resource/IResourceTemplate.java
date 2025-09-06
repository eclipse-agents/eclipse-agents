package org.eclipse.mcp.resource;

import io.modelcontextprotocol.spec.McpSchema.ResourceLink;
import io.modelcontextprotocol.util.DefaultMcpUriTemplateManager;

public interface IResourceTemplate<T, U> extends IResourceAdapter<U> {

	public IResourceTemplate<T, U> fromUri(String uri);
	
	public IResourceTemplate<T, U> fromModel(T object);
	
	public String[] getTemplates();
	
	public T getModel();
	
	public ResourceLink toResourceLink();
	
	public String toUri();
	
	public String toContent();
	
	public default boolean matches(String uri) {
		for (String template: getTemplates()) {
			if (new DefaultMcpUriTemplateManager(template).matches(uri)) {
				return true;
			}
		}
		return false;
	}
}
