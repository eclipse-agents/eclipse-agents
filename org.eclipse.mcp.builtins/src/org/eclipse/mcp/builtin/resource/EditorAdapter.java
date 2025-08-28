package org.eclipse.mcp.builtin.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.mcp.MCPException;
import org.eclipse.mcp.builtins.Activator;
import org.eclipse.mcp.builtins.json.Editor;
import org.eclipse.mcp.factory.IResourceAdapter;
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
import io.modelcontextprotocol.spec.McpSchema.ResourceLink;
import io.modelcontextprotocol.util.DefaultMcpUriTemplateManager;

public class EditorAdapter implements IResourceAdapter<IEditorReference> {

	final String template = "eclipse://editor/{name}";
	final String prefix = template.substring(0, template.indexOf("{"));
	IEditorReference editorReference = null;
	
	public EditorAdapter() {}

	public EditorAdapter(IEditorReference editorReference) {
		this.editorReference = editorReference;
	}
	
	public EditorAdapter(String uri) {
		DefaultMcpUriTemplateManager tm = new DefaultMcpUriTemplateManager(template);
		if (tm.matches(uri)) {
			Map<String, String> variables = tm.extractVariableValues(uri);
			String name = variables.get("name");
			name = URLDecoder.decode(name);

			for (IWorkbenchWindow window: PlatformUI.getWorkbench().getWorkbenchWindows()) {
				for (IWorkbenchPage page: window.getPages()) {
					for (IEditorReference reference: page.getEditorReferences()) {
						if (reference.getName().equals(name)) {
							this.editorReference = reference;
						}
					}
				}
			}
		}
		
		if (editorReference == null) {
			throw new MCPException("uri not resolved: " + uri);
		}
	}

	@Override
	public String getTemplate() {
		return template;
	}
	
	@Override
	public IResourceAdapter<IEditorReference> fromUri(String uri) {
		return new EditorAdapter(uri);
	}
	
	public IResourceAdapter<IEditorReference> fromEditorName(String name) {
		return new EditorAdapter(prefix + URLEncoder.encode(name));
	}

	@Override
	public IResourceAdapter<IEditorReference> fromModel(IEditorReference console) {
		return new EditorAdapter(console);
	}

	@Override
	public boolean supportsChildren() {
		return false;
	}

	@Override
	public IResourceAdapter<IEditorReference>[] getChildren(int depth) {
		return new EditorAdapter[0];
	}

	@Override
	public IEditorReference getModel() {
		return editorReference;
	}

	@Override
	public Object toJson() {
		return new Editor(editorReference);
	}

	@Override
	public ResourceLink toResourceLink() {
		McpSchema.ResourceLink.Builder builder =  McpSchema.ResourceLink.builder();
		
		builder
			.uri(toUri())
			.name(editorReference.getTitle())
			.description("Content of an Eclipse IDE Editor");
		
		Activator.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				IEditorPart part = editorReference.getEditor(false);
				if (part instanceof ITextEditor) {
					ITextEditor textEditor = (ITextEditor)part;
					IDocument document = textEditor.getDocumentProvider().getDocument(part.getEditorInput());
					
					builder
						.mimeType("text/plain")
						.size((long)document.getLength());
				} 
			}
		});

		return builder.build();
		
	}

	@Override
	public String toUri() {
		return prefix + URLEncoder.encode(editorReference.getTitle());
	}

	@Override
	public String toContent() {
		
		StringBuffer result = new StringBuffer();
		Activator.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				IEditorPart part = editorReference.getEditor(true);
				if (part instanceof ITextEditor) {
					ITextEditor textEditor = (ITextEditor)part;
					IDocument document = textEditor.getDocumentProvider().getDocument(part.getEditorInput());
					result.append(document.get());
				} else {
					try {
						IEditorInput input = editorReference.getEditorInput();
						if (input instanceof IFileEditorInput) {
							IFile file = ((IFileEditorInput)input).getFile();
							
							try (InputStreamReader reader = new InputStreamReader(
									file.getContents(), file.getCharset())) {
							       
								BufferedReader breader = new BufferedReader(reader);
								result.append(breader.lines().collect(Collectors.joining("\n"))); //$NON-NLS-1$
								
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
			}
		});
		return result.toString();
	}
}
