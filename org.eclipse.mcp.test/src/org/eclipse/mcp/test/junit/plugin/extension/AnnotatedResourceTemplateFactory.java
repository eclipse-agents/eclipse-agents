package org.eclipse.mcp.test.junit.plugin.extension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.mcp.experimental.annotated.MCPAnnotatedResourceTemplateFactory;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedResourceTemplateFactory.ResourceTemplate;

import io.modelcontextprotocol.spec.McpSchema;

@ResourceTemplate (
		uriTemplate = "db:///{schema}/{table}",
		name = "Table",
		title = "A Database Table",
		description = "A Database Table",
		mimeType = "application/json",
		roles = {McpSchema.Role.USER, McpSchema.Role.ASSISTANT},
		priority  = 0.5)
@ResourceTemplate (
		uriTemplate = "db:///{schema}/{table}/{column}",
		name = "Table Column",
		description = "")
public class AnnotatedResourceTemplateFactory extends MCPAnnotatedResourceTemplateFactory {

	@Override
	public List<String> completionReq(String argumentName, String argumentValue, String uri, Map<String, String> arguments) {
		return Arrays.asList(new String[] {
				argumentValue + "-1",
				argumentValue + "-2"
		});
	}

	@Override
	public String[] readResource(String url) {
		return new String[] {
				url + "-1",
				url + "-2"
		};
	}

}
