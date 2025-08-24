package org.eclipse.mcp.builtins.json;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonClassDescription("A single text replacement")
public class TextReplacement {

	@JsonProperty
	@JsonPropertyDescription("the text to insert into editor")
	public String text;

	@JsonProperty
	@JsonPropertyDescription("the character offset to insert the text")
	public int offset;
	
	@JsonProperty
	@JsonPropertyDescription("the length of text after the offset to remove")
	public int length;

}
