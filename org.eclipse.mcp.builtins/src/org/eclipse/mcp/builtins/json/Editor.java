package org.eclipse.mcp.builtins.json;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.mcp.Activator;
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
import org.eclipse.ui.internal.EditorReference;
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
	@JsonPropertyDescription("An Eclipse IDE Editor")
	McpSchema.ResourceLink editor;
	
	@JsonProperty(required = false)
	@JsonPropertyDescription("If the editor is open on a file, the file being edited")
	McpSchema.ResourceLink file;
	
	@JsonProperty
	@JsonPropertyDescription("Whether this is the editor has the user's focus")
	boolean isActive;
	
	@JsonProperty
	@JsonPropertyDescription("Whether editor contains unsaved changes")
	boolean isDirty;
	
	public Editor() {
		super();
	}

	public Editor(IEditorPart editorPart) {
		super();
		
		if (editor != null) {
			this.name = editorPart.getTitle();
			this.isDirty = editorPart.isDirty();

			//TODO does active check may fail if Eclipse doesn't have focus
			Activator.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					IWorkbench workbench = PlatformUI.getWorkbench();
					if (workbench != null) {
						IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
						if (window != null) {
							IWorkbenchPage page = window.getActivePage();
							if (page != null) {
								if (editorPart == page.getActiveEditor()) {
									Editor.this.isActive = true;
									System.out.println("isActive: " + Editor.this.isActive);
								}
							}
						}
					}
				}
			});

			IEditorInput input = editorPart.getEditorInput();
			input.getName();
			
			if (input instanceof IFileEditorInput) {
				IFile ifile = ((IFileEditorInput)input).getFile();
				file = new RelativeFileAdapter((IResource)ifile).toResourceLink();
			}
			
			editor = new EditorAdapter().fromEditorName(name).toResourceLink();

		}
	}

	public Editor(IEditorReference reference) {
		this(reference.getEditor(false));
		
		if (reference.getEditor(false) == null) {
			this.name = reference.getTitle();
			this.isDirty = reference.isDirty();
			this.editor = new EditorAdapter(reference).toResourceLink();
		}
	}
}
