package org.eclipse.mcp.builtin.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.mcp.IMCPTool;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.google.gson.Gson;

public class ListEditors implements IMCPTool {

	Gson gson;
	public ListEditors() {
		super();
		gson = new Gson();
	}
	
	@Override
	public String[] apply(Map<String, Object> t, DialogSettings[] settings) {
		List<String> result = new ArrayList<String>();
		for (IWorkbenchWindow ww: PlatformUI.getWorkbench().getWorkbenchWindows()) {
			for (IWorkbenchPage page: ww.getPages()) {
				for (IEditorReference reference: page.getEditorReferences()) {
					result.add(gson.toJson(new Editor(reference)));
				}
			}
		}
		return (String[]) result.toArray();
	}
	
	public class Editor {
		String name;
		String file;
		boolean isActive;
		public Editor(IEditorReference ref) {
			this.name = ref.getName();
			ref.getContentDescription();
			ref.getPartName();
			ref.getTitle();
			ref.getId();
			ref.isDirty();
		}
	}
}
