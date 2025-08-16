package org.eclipse.mcp.builtin.resource.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedResourceTemplateFactory;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedResourceTemplateFactory.ResourceTemplate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

@ResourceTemplate (
		uriTemplate = "editor:///{name}",
		name = "Eclipse Editor",
		description = "Content of an Eclipse Text Editor")
public class EditorTemplates extends MCPAnnotatedResourceTemplateFactory {

	@Override
	public List<String> completionReq(String argumentName, String argumentValue, String uri, Map<String, String> arguments) {
		List<String> result = new ArrayList<String>();
		if (argumentName.equals("name")) {
			for (IWorkbenchWindow ww: PlatformUI.getWorkbench().getWorkbenchWindows()) {
				for (IWorkbenchPage page: ww.getPages()) {
					for (IEditorReference reference: page.getEditorReferences()) {
						result.add(reference.getName());
					}
				}
			}
		}
		return result;
	}

	@Override
	public String[] readResource(String url) {
		String name = url.substring(10);
		List<String> result = new ArrayList<String>();
		for (IWorkbenchWindow ww: PlatformUI.getWorkbench().getWorkbenchWindows()) {
			for (IWorkbenchPage page: ww.getPages()) {
				for (IEditorReference reference: page.getEditorReferences()) {
					if (reference.getName().equals(name)) {
						IEditorPart part = reference.getEditor(true);
						if (part == null) {
							part = (IEditorPart)reference.getPart(true);
						}
						if (part instanceof ITextEditor) {
							IDocument doc = ((ITextEditor)part).getDocumentProvider().getDocument(part.getEditorInput());
							return new String[] { doc.get() };
						}
					}
				}
			}
		}
		return new String[0];
	}

}
