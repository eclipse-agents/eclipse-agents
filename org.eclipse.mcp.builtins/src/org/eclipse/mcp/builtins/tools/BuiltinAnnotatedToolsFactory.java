package org.eclipse.mcp.builtins.tools;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mcp.builtins.json.Console;
import org.eclipse.mcp.builtins.json.Consoles;
import org.eclipse.mcp.builtins.json.Editor;
import org.eclipse.mcp.builtins.json.Editors;
import org.eclipse.mcp.builtins.json.TextEditorSelection;
import org.eclipse.mcp.builtins.json.TextSelection;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedToolFactory;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

public class BuiltinAnnotatedToolsFactory extends MCPAnnotatedToolFactory {

	
	public BuiltinAnnotatedToolsFactory(Method method, Tool toolAnnotation) {
		super(method, toolAnnotation);
	}

	@Tool (
			name = "currentSelection",
			title = "Currrent Selection",
			description = "Return the text selection of active Eclipse IDE text editor")
	public TextEditorSelection currentSelection(
			@ToolArg(name = "adsf", description = "do this")
			TextSelection selection2) {
		TextEditorSelection selection = null;
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null) {
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			if (window != null) {
				IWorkbenchPage page = window.getActivePage();
				if (page != null && page.getActiveEditor() != null) {
					TextEditorSelection tes = new TextEditorSelection();
					tes.editor = new Editor(page.getActiveEditor());
					tes.textSelection = new TextSelection(page.getActiveEditor().getEditorSite().getSelectionProvider().getSelection());
				}
			}
		}
		return selection;
	}
	
	@Tool (
			name = "listEditors",
			title = "List Editors",
			description = "List open Eclipse IDE text editors")
	public Editors listEditors() {
		Editors editors = new Editors();
		List<Editor> result = new ArrayList<Editor>();
		
		for (IWorkbenchWindow ww: PlatformUI.getWorkbench().getWorkbenchWindows()) {
			for (IWorkbenchPage page: ww.getPages()) {
				for (IEditorReference reference: page.getEditorReferences()) {
					result.add(new Editor(reference));
				}
			}
		}

		editors.editors = result.toArray(new Editor[0]); 
		return editors;
	}
	
	@Tool (
			name = "listConsoles",
			title = "List Consoles",
			description = "List open Eclipse IDE consoles")
	public Consoles listConsoles() {
		
		List<Console> result = new ArrayList<Console>();
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		for (IConsole console: manager.getConsoles()) {
			result.add(new Console(console.getName(), console.hashCode(),  console.getType()));
		}
		Consoles consoles = new Consoles();
		consoles.consoles = result.toArray(new Console[0]);
		return consoles;
	}
	
//	@Tool
//	public Problem[] listProblems() {
//		return new Problem[0];
//	}
}