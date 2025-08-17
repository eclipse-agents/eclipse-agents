package org.eclipse.mcp.builtins.json;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.modelcontextprotocol.spec.McpSchema;

@JsonClassDescription("An Eclipse IDE text editor")
public class Editor {

	@JsonProperty()
	@JsonPropertyDescription("Title of this editor")
	String name;
	
	@JsonProperty
	@JsonPropertyDescription("The current content of this editor")
	McpSchema.ResourceLink resource;
	
	@JsonProperty
	@JsonPropertyDescription("The file being edited")
	McpSchema.ResourceLink file;
	
	@JsonProperty
	@JsonPropertyDescription("Whether this is the editor the user is currently using")
	boolean isActive;
	
	@JsonProperty
	@JsonPropertyDescription("Whether this editor's buffer has unsaved changes")
	boolean isDirty;
	
	public Editor() {
		super();
	}

	public Editor(IEditorPart editor) {
		super();
		
		if (editor != null) {
			this.name = editor.getTitle();
			this.isDirty = editor.isDirty();
			
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null) {
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				if (window != null) {
					IWorkbenchPage page = window.getActivePage();
					if (page != null) {
						if (editor == page.getActiveEditor()) {
							this.isActive = true;
						}
					}
				}
			}
			
			IEditorInput input = editor.getEditorInput();
			input.getName();
			
			if (input instanceof IFileEditorInput) {
				IFile ifile = ((IFileEditorInput)input).getFile();
				file = Util.fileToResourceLink(ifile);
			}
			
			if (editor instanceof ITextEditor) {
				resource = Util.editorToResourceLink((ITextEditor)editor);
			}
		}
	}

	public Editor(IEditorReference reference) {
		this(reference.getEditor(false));
		
		if (reference.getEditor(false) == null) {
			this.name = reference.getTitle();
			this.isDirty = reference.isDirty();
		}
	}
}
