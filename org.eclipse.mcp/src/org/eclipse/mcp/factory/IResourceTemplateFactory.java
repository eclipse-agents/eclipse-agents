package org.eclipse.mcp.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpServerFeatures.SyncCompletionSpecification;
import io.modelcontextprotocol.server.McpServerFeatures.SyncResourceSpecification;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CompleteRequest;
import io.modelcontextprotocol.spec.McpSchema.CompleteResult;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceRequest;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.Resource;
import io.modelcontextprotocol.spec.McpSchema.ResourceContents;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;

/**
 * Interface for contributing MCP Resource Templates containing uri's with substitution variables
 * and support for completion assistance of variable values
 */
public interface IResourceTemplateFactory extends IFactory{
		
	public McpSchema.ResourceTemplate[] createResourceTemplates();
	
	public default SyncResourceSpecification getResourceTemplateSpecification(McpSchema.ResourceTemplate template) {
		Resource resource = new Resource(template.uriTemplate(), template.name(), template.description(), template.mimeType(), template.annotations());
		return new McpServerFeatures.SyncResourceSpecification(resource, this::readResource);
	}
	
	public default SyncCompletionSpecification createCompletionSpecification(McpSchema.ResourceTemplate template) {
		return new McpServerFeatures.SyncCompletionSpecification(
				new McpSchema.ResourceReference(template.uriTemplate()), this::completionReq);
	}
	
	public default CompleteResult completionReq(McpSyncServerExchange exchange, CompleteRequest request) {
		String uri = request.ref().identifier();
		Map<String, String> arguments = request.context().arguments();
		List<String> results = completionReq(request.argument().name(), request.argument().value(), uri, arguments);
		return new McpSchema.CompleteResult(
	            new CompleteResult.CompleteCompletion(
	             results,
	             results.size(), // total
	             false // hasMore
	     ));
	}
	
	public default ReadResourceResult readResource(McpSyncServerExchange exchange, ReadResourceRequest request) {
		List<ResourceContents> contents = new ArrayList<ResourceContents>();
		for (String s: readResource(request.uri())) {
			contents.add(new TextResourceContents(
				request.uri(),
				getMimeType(request.uri()),
				s));
		}
		return new ReadResourceResult(contents);
	}
	
	/**
	 * Simplistic handler for fulfilling template variable completion requests.
	 * Override the other completionReq for more advanced features 
	 * @param argumentName
	 * @param argumentValue
	 * @return
	 */
	public List<String> completionReq(String argumentName, String argumentValue, String uri, Map<String, String> arguments);
	
	/**
	 * Simplistic helper method for turning a URI to an String[] of content
	 * For advanced operations, see the other <code>readResource<code> method
	 * @param url
	 * @return
	 */
	public String[] readResource(String url);
	
	/**
	 * @param uri
	 * @return mime type of this uri
	 */
	public String getMimeType(String uri);
}