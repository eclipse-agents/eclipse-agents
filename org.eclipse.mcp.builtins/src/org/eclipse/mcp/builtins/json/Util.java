package org.eclipse.mcp.builtins.json;

import java.util.Arrays;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.ITextEditor;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.Annotations;
import io.modelcontextprotocol.spec.McpSchema.Role;

public class Util {

	public static McpSchema.ResourceLink editorToResourceLink(ITextEditor editor) {
		IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		
		return McpSchema.ResourceLink.builder()
				.uri(editorToURI(editor))
				.name(editor.getTitle())
				.description("Content of an Eclipse Text Editor")
				.mimeType("text/plain")
				.size((long)document.getLength())
				.annotations(new Annotations(Arrays.asList(Role.ASSISTANT, Role.USER), 1.0))
				.build();
		
	}
	public static String editorToURI(ITextEditor editor) {
		return "eclipse://editor/" + editor.getTitle();
	}
	
	public static McpSchema.ResourceLink fileToResourceLink(IFile file) {

		McpSchema.ResourceLink.Builder builder =  McpSchema.ResourceLink.builder()
				.uri(resourceToURI(file))
				.name(file.getName())
				.description("Content of an file in an Eclipse workspace")
				.mimeType("text/plain")
				.annotations(new Annotations(Arrays.asList(Role.ASSISTANT, Role.USER), 1.0));
		
		try {
			IFileStore store = EFS.getStore(file.getLocationURI());
			IFileInfo info = store.fetchInfo();
			builder.size(info.getLength());
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return builder.build();
	}
	
	public static McpSchema.ResourceLink containerToResourceLink(IContainer container) {

		McpSchema.ResourceLink.Builder builder =  McpSchema.ResourceLink.builder()
				.uri(resourceToURI(container))
				.name(container.getName())
				.description("Content of an folder in an Eclipse workspace")
				.annotations(new Annotations(Arrays.asList(Role.ASSISTANT, Role.USER), 1.0));

		return builder.build();
	}
	
	public static  String resourceToURI(IResource resource) {
		resource.getLocation();
		resource.getLocationURI();
		resource.getRawLocation();
		return resource.getLocationURI().toString();
	}
}
