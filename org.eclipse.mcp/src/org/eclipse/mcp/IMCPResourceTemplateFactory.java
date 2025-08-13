package org.eclipse.mcp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import io.modelcontextprotocol.spec.McpSchema.ResourceTemplate;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;

public abstract class IMCPResourceTemplateFactory {

	ResourceTemplate[] templates;
	
	public IMCPResourceTemplateFactory() {
		templates = createResourceTemplates();
	}
	public String getId() {
		return getClass().getCanonicalName();
	}
	
	public abstract ResourceTemplate[] createResourceTemplates();
	
	public SyncResourceSpecification getResourceTemplateSpecification(ResourceTemplate template) {
		McpSchema.Annotations annotations = new McpSchema.Annotations(Arrays.asList(McpSchema.Role.valueOf("")), 0.5);
		Resource resource = new Resource(template.uriTemplate(), template.name(), template.description(), template.mimeType(), annotations);
		return new McpServerFeatures.SyncResourceSpecification(resource, this::readResource);
	}
	
	public SyncCompletionSpecification createCompletionSpecification(ResourceTemplate template) {
		return new McpServerFeatures.SyncCompletionSpecification(
				new McpSchema.ResourceReference(template.uriTemplate()), this::completionReq);
	}
	
	public CompleteResult completionReq(McpSyncServerExchange exchange, CompleteRequest request) {
		List<String> results = completionReq(request.argument().name(), request.argument().value());
		return new McpSchema.CompleteResult(
	            new CompleteResult.CompleteCompletion(
	             results,
	             results.size(), // total
	             false // hasMore
	     ));
	}
	
	public List<String> completionReq(String argumentName, String argumentValue) {
		return new ArrayList<String>();
	}
	
	public ReadResourceResult readResource(McpSyncServerExchange exchange, ReadResourceRequest request) {
		List<ResourceContents> contents = new ArrayList<ResourceContents>();
		for (String s: readResource(request.uri())) {
			contents.add(new TextResourceContents(
				request.uri(),
				findMimeType(request.uri()),
				s));
		}
		return new ReadResourceResult(contents);
	}
	
	public abstract String[] readResource(String url);

	public String findMimeType(String uri) {
		for (ResourceTemplate template: templates) {
			if (template.uriTemplate().equals(uri)) {
				return template.mimeType();
			}
		}
		return "text/plain";
	}
}