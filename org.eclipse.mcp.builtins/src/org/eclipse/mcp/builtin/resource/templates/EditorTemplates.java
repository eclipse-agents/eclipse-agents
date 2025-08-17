package org.eclipse.mcp.builtin.resource.templates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedResourceTemplateFactory;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedResourceTemplateFactory.ResourceTemplate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

@ResourceTemplate (
		uriTemplate = "eclipse://editor/{name}",
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
		String[] split = url.split("/");
		String editorName = split[split.length - 1];
		List<String> result = new ArrayList<String>();
		for (IWorkbenchWindow ww: PlatformUI.getWorkbench().getWorkbenchWindows()) {
			for (IWorkbenchPage page: ww.getPages()) {
				for (IEditorReference reference: page.getEditorReferences()) {
					if (reference.getName().equals(editorName)) {
						IEditorPart part = reference.getEditor(true);
						if (part == null) {
							try {
								IEditorInput input = reference.getEditorInput();
								if (input instanceof IFileEditorInput) {
									IFile file = ((IFileEditorInput)input).getFile();
									
									try (InputStreamReader reader = new InputStreamReader(
											file.getContents(), file.getCharset())) {
									       
										BufferedReader breader = new BufferedReader(reader);
										String read = breader.lines().collect(Collectors.joining("\n")); //$NON-NLS-1$
										if (read != null) {
											result.add(read);
										}
									}catch (UnsupportedEncodingException e) {
										e.printStackTrace();
									} catch (CoreException e) {
										e.printStackTrace();
									} catch (IOException e1) {
										e1.printStackTrace();
									}

								}
							} catch (PartInitException e) {
								e.printStackTrace();
							}
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
