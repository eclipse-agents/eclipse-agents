package org.eclipse.mcp.builtins.json;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonClassDescription("")
public class TextEditorSelection {

	@JsonPropertyDescription("Selected Text Editor")
	@JsonProperty
	public Editor editor;
	
	@JsonProperty
	@JsonPropertyDescription("Selected text")
	public TextSelection textSelection;
}
