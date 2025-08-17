package org.eclipse.mcp.builtins.json;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class TextEditorSelection {

	@JsonPropertyDescription("Selected Text Editor")
	public Editor editor;
	
	@JsonPropertyDescription("Selected text")
	public TextSelection textSelection;
}
