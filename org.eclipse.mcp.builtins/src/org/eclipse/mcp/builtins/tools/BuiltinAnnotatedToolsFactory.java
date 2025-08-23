package org.eclipse.mcp.builtins.tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.patch.ApplyPatchOperation;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.mcp.MCPException;
import org.eclipse.mcp.builtin.resource.RelativeFileAdapter;
import org.eclipse.mcp.builtins.Activator;
import org.eclipse.mcp.builtins.json.Console;
import org.eclipse.mcp.builtins.json.Consoles;
import org.eclipse.mcp.builtins.json.Editor;
import org.eclipse.mcp.builtins.json.Editors;
import org.eclipse.mcp.builtins.json.Problems;
import org.eclipse.mcp.builtins.json.Resources;
import org.eclipse.mcp.builtins.json.TextEditorSelection;
import org.eclipse.mcp.builtins.json.TextSelection;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedToolFactory;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
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
	
	@Tool (
			name = "listProjects",
			title = "List Projects",
			description = "List open Eclipse IDE projects")
	public Resources listProjects() {
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return new Resources(workspace.getRoot(), 0);
	}
	
	@Tool (
			name = "listChildResources",
			title = "List Child Resources",
			description = "List child resources of an Eclipse project or folder")
	public Resources listChildResources(
			@ToolArg(name = "resourceURI", description = "URI of an eclipse project or folder")
			String resourceURI,
			@ToolArg(name = "depth", description = "0 for immediate children, 1 for children and grandchildren, 2 for infinite depth", required = false)
			int depth) {
		
		Object resolved = Activator.getDefault().getEclipseResource(resourceURI);
		if (resolved == null) {
			throw new MCPException("The uri could not be resolved");
		} else if (resolved instanceof IContainer) {
			return new Resources((IContainer)resolved, depth);
		} else if (resolved instanceof IFile) {
			throw new MCPException("the resource is a file.  Only folders can have children");
		} else if (resolved instanceof File) {
			if (!((File)resolved).isFile()) {
				//TODO 
				throw new MCPException("Absolute file paths not supported at this time");
			} else {
				throw new MCPException("the resource is a file.  Only folders can have children");
			}
		} else {
			throw new MCPException("No text content could be read from that resource");
		}
	}
	
	@Tool (
			name = "readResource",
			title = "Read Resources",
			description = "Returns the contents of a file, editor, or console URI")
	public String readResource(
			@ToolArg(name = "uri", description = "URI of an eclipse file, editor or console")
			String uri) {
	
		String result =  Activator.getDefault().getResourceContent(uri);
		if (result == null) {
			Object resolved = Activator.getDefault().getEclipseResource(uri);
			if (resolved == null) {
				throw new MCPException("The uri could not be resolved");
			} else if (resolved instanceof IContainer) {
				throw new MCPException("The URI resolved to a folder or project.  Only files can be read");
			} else if (resolved instanceof File) {
				if (!((File)resolved).isFile()) {
					throw new MCPException("The URI resolved to a folder.  Only files can be read");
				}
			} else {
				throw new MCPException("No text content could be read from that resource");
			}
			throw new MCPException("No text content could be read from that resource");
		}
		return result;
	}
	

/**
diff --git a/com.ibm.systemz.db2.samples/cobol/example1/README.md b/com.ibm.systemz.db2.samples/cobol/example1/README.md
index deb881a..a2199c5 100644
--- a/com.ibm.systemz.db2.samples/cobol/example1/README.md
+++ b/com.ibm.systemz.db2.samples/cobol/example1/README.md
@@ -4,7 +4,7 @@
 
 Following environment was used : MVS System: VM30094.POK.STGLABS.IBM.COM
 
-DB2 Subsystem name:port DB2D:61013 (DB2 version 13)
+DB2 Subsys name:port DB2D:61013 (DB2 version 13)
 
 **Instructions:**
 
@@ -50,7 +50,7 @@
 
 **Explanation of changes applied in a PROCLIB**
 
-Following definitions for //SYSTSIN should be applied in ELACFSP and ELACFSQL members of your-proclib   :
+Following deitions for //SYSTSIN should be applied in ELACFSP and ELACFSQL members of your-proclib   :
  
      //SYSTSIN DD *
 
*/

/**
patch`: `--- java/src/java/Main.java
+++ java/src/java/Main.java
@@ -5,8 +5,8 @@ public class Main {
 	public static void main(String[] args) {
 		// TODO Auto-generated method stub
 		System.out.println(\"asdf\");
-		System.out.pintln(\"asdf\");
+		System.out.println(\"asdf\");
 		System.out.println(\"asdf\");
-		System.ot.println(\"asdf\");
+		System.out.println(\"asdf\");
 	}
 
 }
 */
//file://workspace/java/src/java/Main.java
//	 @Tool (
//				name = "applyPatch",
//				title = "Apply Patch",
//				description = "Apply a git unified diff format patch to ?workspace")
//	public void applyPatch(
//			@ToolArg(name = "patch", description = "A unified diff format patch to to workspace root")
//			String patch,
//			@ToolArg(name = "resourceURI", description = "uri to apply the patch to")
//			String resourceUrl) {
//
//		System.out.println(patch);
//		System.out.println(resourceUrl);
//		IResource resource = resourceUrl == null ? null : 
//				new RelativeFileAdapter().uriToEclipseObject(resourceUrl);
//
//		IStorage patchStorage = new IStorage() {
//			@Override
//			public <T> T getAdapter(Class<T> arg0) {
//				return null;
//			}
//			@Override
//			public InputStream getContents() throws CoreException {
//				return new ByteArrayInputStream(patch.getBytes()) {
//
//					@Override
//					public synchronized int read() {
//						// TODO Auto-generated method stub
//						return super.read();
//					}
//
//					@Override
//					public synchronized int read(byte[] b, int off, int len) {
//						// TODO Auto-generated method stub
//						return super.read(b, off, len);
//					}
//
//					@Override
//					public synchronized byte[] readAllBytes() {
//						// TODO Auto-generated method stub
//						return super.readAllBytes();
//					}
//
//					@Override
//					public int readNBytes(byte[] b, int off, int len) {
//						// TODO Auto-generated method stub
//						return super.readNBytes(b, off, len);
//					}
//					
//				};
//			}
//			@Override
//			public IPath getFullPath() {
//				return null;
//			}
//			@Override
//			public String getName() {
//				return "MCP Patch";
//			}
//			@Override
//			public boolean isReadOnly() {
//				return true;
//			}
//		};
//		
//		Activator.getDisplay().asyncExec(new Runnable() {
//			@Override
//			public void run() {
//				IWorkbenchPart activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
//				
//				ApplyPatchOperation oper = new ApplyPatchOperation(activePart, patchStorage, resource, new CompareConfiguration());
//				
//				
//				BusyIndicator.showWhile(Display.getDefault(), oper);
//			}
//		});
//	}
	
	

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
     public Problems listProblems(
    		 @ToolArg(name = "resourceURI", description = "URI file in the Eclipse workspace")
    		 String resourceURI) {
    	 
    	 RelativeFileAdapter adapter= new RelativeFileAdapter();
    	 IResource resource = adapter.uriToEclipseObject(resourceURI);
    	 if (resource instanceof IResource) {
    		return new Problems(resource);
    	 } else if (resource instanceof IEditorReference) {
    		 return new Problems((IEditorReference) resource);
    	 }
    	
    	throw new MCPException("The resource URI could not be resolved");
 		
     }

}