package org.eclipse.mcp.builtins.tools;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mcp.builtins.json.Console;
import org.eclipse.mcp.builtins.json.Editor;
import org.eclipse.mcp.builtins.json.Selection;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedToolFactory;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

import com.google.gson.Gson;

public class BuiltinAnnotatedToolsFactory extends MCPAnnotatedToolFactory {

	
	public BuiltinAnnotatedToolsFactory(Method method, Tool toolAnnotation) {
		super(method, toolAnnotation);
	}

	@Tool (
			id = " org.eclipse.mcp.builtins.currentSelection", 
			description = "Return active editor and its text selection")
	public String[] currentSelection() {
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
	
	@Tool (
			id = " org.eclipse.mcp.builtins.listEditors", 
			description = "List open editors")
	public String[] listEditors() {
		List<String> result = new ArrayList<String>();
		Gson gson = new Gson();
		for (IWorkbenchWindow ww: PlatformUI.getWorkbench().getWorkbenchWindows()) {
			for (IWorkbenchPage page: ww.getPages()) {
				for (IEditorReference reference: page.getEditorReferences()) {
					result.add(gson.toJson(new Editor(reference)));
				}
			}
		}
		return result.toArray(new String[0]);
	}
	
	@Tool (
			id = " org.eclipse.mcp.builtins.listConsoles", 
			description = "List open editors")
	public String[] listConsoles() {
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		Gson gson = new Gson();
		List<String> result = new ArrayList<String>();
		for (IConsole console: manager.getConsoles()) {
			Console c = new Console(console.getName(), console.hashCode(),  console.getType());
			result.add(gson.toJson(c));
		}
		return result.toArray(new String[0]);
	}
}