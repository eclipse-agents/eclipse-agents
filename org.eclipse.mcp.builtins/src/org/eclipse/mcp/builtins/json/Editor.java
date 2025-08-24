package org.eclipse.mcp.builtins.json;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.mcp.builtin.resource.EditorAdapter;
import org.eclipse.mcp.builtin.resource.RelativeFileAdapter;
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
	
	@JsonProperty(required = false)
	@JsonPropertyDescription("The contents of the text editor")
	McpSchema.ResourceLink buffer;
	
	@JsonProperty(required = false)
	@JsonPropertyDescription("The file being edited containing the last saved changes")
	McpSchema.ResourceLink file;
	
	@JsonProperty
	@JsonPropertyDescription("Whether this is the editor has the user's focus")
	boolean isActive;
	
	@JsonProperty
	@JsonPropertyDescription("Whether text editor contains unsaved changes")
	boolean isDirty;
	
	public Editor() {
		super();
	}

	public Editor(IEditorPart editor) {
		super();
		
		if (editor != null) {
			this.name = editor.getTitle();
			this.isDirty = editor.isDirty();

			//TODO does active check may fail if Eclipse doesn't have focus
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
				file = new RelativeFileAdapter().eclipseObjectToResourceLink((IResource)ifile);
			}
			
			if (editor instanceof ITextEditor) {
				buffer = new EditorAdapter().eclipseObjectToResourceLink((ITextEditor)editor);
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
	
	public boolean isValid() {
		return file != null && buffer != null;
	}
}
