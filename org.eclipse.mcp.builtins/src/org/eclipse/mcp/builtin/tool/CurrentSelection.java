package org.eclipse.mcp.builtin.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.mcp.IElementProperties;
import org.eclipse.mcp.IMCPTool;
import org.eclipse.mcp.builtins.json.Editor;
import org.eclipse.mcp.builtins.json.Selection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.google.gson.Gson;

public class CurrentSelection implements IMCPTool {

	@Override
	public String[] apply(Map<String, Object> args, IElementProperties properties) {
		List<String> result = new ArrayList<String>();
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null) {
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			if (window != null) {
				IWorkbenchPage page = window.getActivePage();
				if (page != null && page.getActiveEditor() != null) {
					Editor editor = new Editor(page.getActiveEditor());
					Selection selection = new Selection(page.getActiveEditor().getEditorSite().getSelectionProvider().getSelection());
					Gson gson = new Gson();
					result.add(gson.toJson(editor));
					result.add(gson.toJson(selection));
				}
			}
		}
		return result.toArray(String[]::new);
	}
}
