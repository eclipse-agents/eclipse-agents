package org.eclipse.mcp.builtins.json;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IMarkSelection;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonClassDescription("Range of characters selected in a text editor")
public class TextSelection {

	@JsonPropertyDescription("position of the first selected character")
	int offset;
	@JsonPropertyDescription("length of the text selection")
	int length;
	@JsonPropertyDescription("line of the offset of the selected text")
	int startLine;
	@JsonPropertyDescription("line of the last character of the selected text")
	int endLine;
	@JsonPropertyDescription("selected text")
	String text;

	public TextSelection(ISelection selection) {
		super();
		if (selection instanceof ITextSelection) {
			this.offset = ((ITextSelection) selection).getOffset();
			this.length = ((ITextSelection) selection).getLength();
			this.startLine = ((ITextSelection) selection).getStartLine();
			this.endLine = ((ITextSelection) selection).getEndLine();
			this.text = ((ITextSelection) selection).getText(); 
		} else if (selection instanceof IMarkSelection) {
			this.offset = ((IMarkSelection) selection).getOffset();
			this.length = ((IMarkSelection) selection).getLength();
			try {
				this.startLine = ((IMarkSelection) selection).getDocument().getLineOfOffset(offset);
				this.endLine = ((IMarkSelection) selection).getDocument().getLineOfOffset(offset + length);
				this.text = ((IMarkSelection) selection).getDocument().get(offset, length);
			} catch (BadLocationException e) {
				e.printStackTrace();
			} 
		}
	}
}
