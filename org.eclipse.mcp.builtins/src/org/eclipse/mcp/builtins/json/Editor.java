package org.eclipse.mcp.builtins.json;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class Editor {

	String name;
	String file;
	String uri;
	boolean isActive;
	boolean isDirty;
	
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
			
			editor.getSite().getId();
			editor.getSite().hashCode();
			IEditorInput input = editor.getEditorInput();
			input.getName();
			
			if (input instanceof IFileEditorInput) {
				IFile ifile = ((IFileEditorInput)input).getFile();
				this.file = ifile.getFullPath().toOSString();
				this.uri = ifile.getRawLocationURI().toString();
				
			} else if (input instanceof IStorageEditorInput) {
				try {
					IPath path = ((IStorageEditorInput)input).getStorage().getFullPath();
					//TODO
				} catch (CoreException e) {
					e.printStackTrace();
				}
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
