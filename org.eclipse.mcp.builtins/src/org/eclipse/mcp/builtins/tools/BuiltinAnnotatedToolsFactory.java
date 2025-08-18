package org.eclipse.mcp.builtins.tools;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mcp.MCPException;
import org.eclipse.mcp.builtins.json.Console;
import org.eclipse.mcp.builtins.json.Consoles;
import org.eclipse.mcp.builtins.json.Editor;
import org.eclipse.mcp.builtins.json.Editors;
import org.eclipse.mcp.builtins.json.Problem;
import org.eclipse.mcp.builtins.json.Problems;
import org.eclipse.mcp.builtins.json.TextEditorSelection;
import org.eclipse.mcp.builtins.json.TextSelection;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedToolFactory;
import org.eclipse.swt.widgets.Display;
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
	public TextEditorSelection currentSelection() {
		final TextEditorSelection selection = new TextEditorSelection();
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				IWorkbench workbench = PlatformUI.getWorkbench();
				if (workbench != null) {
					IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
					if (window != null) {
						IWorkbenchPage page = window.getActivePage();
						if (page != null && page.getActiveEditor() != null) {
							selection.editor = new Editor(page.getActiveEditor());
							selection.textSelection = new TextSelection(page.getActiveEditor().getEditorSite().getSelectionProvider().getSelection());
						}
					}
				}
			}
		});
		
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

//     public void openEditor(String fileUri, String selectionPattern) {
//             
//     }
//     
//     public void closeEditor(String fileUri, String selectionPattern) {
//             
//     }
//     
//     public boolean saveEditor(String editorUri) {
//             
//     }
//
//     public void changeEditorText(String fileUri, String proposedContent, String proposalTitle) {
//     
//     }
       
//     @Tool (
//                     name = "listConsoles",
//                     title = "List Consoles",
//                     description = "List open Eclipse IDE consoles")
//     
//     public Projects getProjects() {
//             
//     }
//     
//     @Tool (
//                     name = "listConsoles",
//                     title = "List Consoles",
//                     description = "List open Eclipse IDE consoles")
//     
//     public String readResource(String uri) {
//             
//     }
//
//     
//     @Tool (
//                     name = "listConsoles",
//                     title = "List Consoles",
//                     description = "List open Eclipse IDE consoles")
//     

     @Tool(title = "listProblems", description = "list Eclipse IDE compilation and configuration problems")
     public Problems listProblems() {
    	 Problems problems = new Problems();
    	 List<Problem> results = new ArrayList<Problem>();
    	 
    	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    	try {
			for (IMarker marker: root.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)) {
				results.add(new Problem(marker));
			}
		} catch (CoreException e) {
			throw new MCPException(e);
		}
    	
    	problems.problems = results.toArray(Problem[]::new);
 		return problems;
     }

}