package org.eclipse.mcp.builtins.json;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IMarkSelection;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;

public class Selection {

	int offset;
	int length;
	int startLine;
	int endLine;
	String text;

	public Selection(ISelection selection) {
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
