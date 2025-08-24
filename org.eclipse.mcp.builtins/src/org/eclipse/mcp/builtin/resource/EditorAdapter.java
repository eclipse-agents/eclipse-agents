package org.eclipse.mcp.builtin.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.Annotations;
import io.modelcontextprotocol.spec.McpSchema.ResourceLink;
import io.modelcontextprotocol.spec.McpSchema.Role;

public class EditorAdapter implements IResourceAdapter<IEditorReference> {

	final String template = "eclipse://editor/{name}";

	@Override
	public String getTemplate() {
		return template;
	}

	@Override
	public String getUniqueTemplatePrefix() {
		return template.substring(0, template.indexOf("{"));
	}

	@Override
	public IEditorReference uriToEclipseObject(String uri) {
		if (uri.startsWith(getUniqueTemplatePrefix())) {
			String name = uri.substring(getUniqueTemplatePrefix().length());
			for (IWorkbenchWindow window: PlatformUI.getWorkbench().getWorkbenchWindows()) {
				for (IWorkbenchPage page: window.getPages()) {
					for (IEditorReference reference: page.getEditorReferences()) {
						if (reference.getName().equals(name)) {
							return reference;
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public Object eclipseObjectToJsonObject(IEditorReference object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceLink eclipseObjectToResourceLink(IEditorReference editor) {
		IEditorPart part = editor.getEditor(true);
		if (part instanceof ITextEditor) {
			return eclipseObjectToResourceLink((ITextEditor)part);
		} else {
			System.err.println("not a text editor");
		}
		return null;
	}
	

	public ResourceLink eclipseObjectToResourceLink(ITextEditor textEditor) {
		textEditor.getDocumentProvider();
		textEditor.getEditorInput();
		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		
		McpSchema.ResourceLink.Builder builder = McpSchema.ResourceLink.builder()
				.uri(toURI(textEditor.getTitle()))
				.name(textEditor.getTitle())
				.description("Content of an Eclipse Text Editor")
				.mimeType("text/plain")
				.size((long)document.getLength());
		
		addAnnotations(builder);
		
		return builder.build();
	}

	@Override
	public String eclipseObjectToURI(IEditorReference ref) {
		return toURI(ref.getTitle());
	}
	
	public String toURI(String editorTitle) {
		return getUniqueTemplatePrefix() + editorTitle;
	}

	@Override
	public String eclipseObjectToResourceContent(IEditorReference ref) {
		String result = "";
		IEditorPart part = ref.getEditor(true);
		if (part instanceof ITextEditor) {
			ITextEditor textEditor = (ITextEditor)part;
			IDocument document = textEditor.getDocumentProvider().getDocument(part.getEditorInput());
			result = document.get();
		} else {
			try {
				IEditorInput input = ref.getEditorInput();
				if (input instanceof IFileEditorInput) {
					IFile file = ((IFileEditorInput)input).getFile();
					
					try (InputStreamReader reader = new InputStreamReader(
							file.getContents(), file.getCharset())) {
					       
						BufferedReader breader = new BufferedReader(reader);
						result = breader.lines().collect(Collectors.joining("\n")); //$NON-NLS-1$
						
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
			System.err.println("not a text editor");
		}
		return result;
	}


}
