package org.eclipse.mcp;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.modelcontextprotocol.spec.McpSchema;

public class Schema {

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonClassDescription("Element of an hierarchical file system")
	public record File (
		
		@JsonProperty
		String name,
		
		@JsonPropertyDescription("Folders may have children")
		@JsonProperty
		boolean isFolder,

		@JsonProperty
		McpSchema.ResourceLink uri) {
	}

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Files (

		@JsonProperty
		File[] files) {
		
	}
}
