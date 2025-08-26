package org.eclipse.mcp.builtins.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.mcp.Activator;
import org.eclipse.mcp.MCPException;
import org.eclipse.mcp.builtin.resource.EditorAdapter;
import org.eclipse.mcp.builtin.resource.RelativeFileAdapter;
import org.eclipse.mcp.builtins.json.Console;
import org.eclipse.mcp.builtins.json.Consoles;
import org.eclipse.mcp.builtins.json.Editor;
import org.eclipse.mcp.builtins.json.Editors;
import org.eclipse.mcp.builtins.json.Problems;
import org.eclipse.mcp.builtins.json.Resources;
import org.eclipse.mcp.builtins.json.Tasks;
import org.eclipse.mcp.builtins.json.TextEditorSelection;
import org.eclipse.mcp.builtins.json.TextReplacement;
import org.eclipse.mcp.builtins.json.TextSelection;
//import org.eclipse.mcp.experimental.annotated.MCPAnnotatedToolFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;

public class BuiltinAnnotatedToolsFactory {


	@McpTool(name = "currentSelection", 
			description = "Return the active Eclipse IDE text editor and its selected text", 
			annotations = @McpTool.McpAnnotations(
					title = "Currrent Selection"))
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
							selection.textSelection = new TextSelection(
									page.getActiveEditor().getEditorSite().getSelectionProvider().getSelection());
						}
					}
				}
			}
		});

		return selection;
	}

	@McpTool(name = "listEditors", 
			description = "List open Eclipse IDE text editors", 
			annotations = @McpTool.McpAnnotations(
					title = "List Editors"))
	public Editors listEditors() {
		Editors editors = new Editors();
		List<Editor> result = new ArrayList<Editor>();

		for (IWorkbenchWindow ww : PlatformUI.getWorkbench().getWorkbenchWindows()) {
			for (IWorkbenchPage page : ww.getPages()) {
				for (IEditorReference reference : page.getEditorReferences()) {
					Editor editor = new Editor(reference);
					if (editor.isValid()) {
						result.add(new Editor(reference));
					}
				}
			}
		}

		editors.editors = result.toArray(new Editor[0]);
		return editors;
	}

	@McpTool(name = "listConsoles",
			description = "List open Eclipse IDE consoles", 
			annotations = @McpTool.McpAnnotations(
					title = "List Consoles"))
	public Consoles listConsoles() {

		List<Console> result = new ArrayList<Console>();
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		for (IConsole console : manager.getConsoles()) {
			result.add(new Console(console.getName(), console.hashCode(), console.getType()));
		}
		Consoles consoles = new Consoles();
		consoles.consoles = result.toArray(new Console[0]);
		return consoles;
	}

	@McpTool(name = "listProjects", 
			description = "List open Eclipse IDE projects", 
			annotations = @McpTool.McpAnnotations(
					title = "List Projects"))
	public Resources listProjects() {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return new Resources(workspace.getRoot(), 0);
	}

	@McpTool(name = "listChildResources",
			description = "List child resources of an Eclipse workspace, project or folder URI", 
			annotations = @McpTool.McpAnnotations(
					title = "List Child Resources"))
	public Resources listChildResources(
			@McpToolParam(
					description = "URI of an eclipse project or folder") 
					String resourceURI,
			@McpToolParam(
					description = "0 for immediate children, 1 for children and grandchildren, 2 for infinite depth", 
					required = false) 
					int depth) {

		Object resolved = Activator.getDefault().getEclipseResource(resourceURI);
		if (resolved == null) {
			throw new MCPException("The uri could not be resolved");
		} else if (resolved instanceof IContainer) {
			return new Resources((IContainer) resolved, depth);
		} else if (resolved instanceof IFile) {
			throw new MCPException("the resource is a file.  Only folders can have children");
		} else if (resolved instanceof File) {
			if (!((File) resolved).isFile()) {
				// TODO
				throw new MCPException("Absolute file paths not supported at this time");
			} else {
				throw new MCPException("the resource is a file.  Only folders can have children");
			}
		} else {
			throw new MCPException("No text content could be read from that resource");
		}
	}

	@McpTool(name = "readResource", 
			description = "Returns the contents of an Eclipse workspace file, editor, or console URI", 
			annotations = @McpTool.McpAnnotations(
					title = "Read Resource"))
	public String readResource(
			@McpToolParam(
					description = "URI of an eclipse file, editor or console") 
					String uri) {

		String result = Activator.getDefault().getResourceContent(uri);
		if (result == null) {
			Object resolved = Activator.getDefault().getEclipseResource(uri);
			if (resolved == null) {
				throw new MCPException("The uri could not be resolved");
			} else if (resolved instanceof IContainer) {
				throw new MCPException("The URI resolved to a folder or project.  Only files can be read");
			} else if (resolved instanceof File) {
				if (!((File) resolved).isFile()) {
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
	 * 
	 * @param fileUri
	 * @param selectionOffset
	 * @param selectionLength
	 * @return
	 */
	@McpTool (name = "openEditor", 
			description = "open an Eclipse IDE editor on a file URI and set an initial text selection", 
			annotations = @McpTool.McpAnnotations(
					title = "Open Editor"))
	public Editor openEditor(
			@McpToolParam(
					description = "Eclipse workspace file uri") 
					String fileUri,
			@McpToolParam(
					description = "offset of the text selection", 
					required = false) 
					int selectionOffset,
			@McpToolParam(
					description = "length of the text selection", 
					required = false) 
					int selectionLength) {

		RelativeFileAdapter adapter = new RelativeFileAdapter();
		IResource resource = adapter.uriToEclipseObject(fileUri);
		final Editor[] result = new Editor[] { null };

		if (resource instanceof IFile) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					try {
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						IWorkbenchPart part = page.getActivePart();
						part.dispose();

						IEditorPart editor = IDE.openEditor(page, (IFile) resource, true);
						result[0] = new Editor(editor);

						if (editor instanceof ITextEditor) {
							try {
								ITextEditor textEditor = (ITextEditor) editor;
								IDocument document = textEditor.getDocumentProvider()
										.getDocument(textEditor.getEditorInput());
								if (selectionOffset >= 0 && document.getLength() > selectionOffset) {
									if (selectionLength > 0
											&& document.getLength() > selectionOffset + selectionLength) {
										textEditor.selectAndReveal(selectionOffset, selectionLength);
									} else {
										textEditor.selectAndReveal(selectionOffset, 0);
									}
								}
							} catch (Exception e) {
								// swallow selection errors
								e.printStackTrace();
							}
						}
						page.activate(editor);
						page.getActivePart();

					} catch (PartInitException e) {
						throw new MCPException(e);
					} catch (CoreException e) {
						throw new MCPException(e);
					}
				}
			});
		} else {
			throw new MCPException("The file URI could not be resolved");
		}

		return result[0];
	}

	@McpTool(name = "closeEditor", 
			description = "close an Eclipse IDE editor", 
			annotations = @McpTool.McpAnnotations(
					title = "Close Editor"))
	public void closeEditor(
			@McpToolParam(
				description = "URI of an open Eclipse editor") 
				String editorUri) {
		EditorAdapter adapter = new EditorAdapter();
		final IEditorReference reference = adapter.uriToEclipseObject(editorUri);

		// TODO close just the editor, not all editors on editor's file
		Activator.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				reference.getPage().closeEditors(new IEditorReference[] { reference }, true);
			}
		});
	}

	@McpTool(name = "saveEditor", 
			description = "save the contents of a dirty Eclipse IDE editor to file", 
			annotations = @McpTool.McpAnnotations(
					title = "Save Editor"))
	public boolean saveEditor(
			@McpToolParam(
			description = "URI of an open Eclipse editor") 
			String editorUri) {
		EditorAdapter adapter = new EditorAdapter();
		final IEditorReference reference = adapter.uriToEclipseObject(editorUri);
		boolean[] result = new boolean[] { false };
		if (reference != null) {
			if (reference.isDirty()) {
				Activator.getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						try {
							IEditorInput input = reference.getEditorInput();
							if (input instanceof IFileEditorInput) {
								IFile ifile = ((IFileEditorInput) input).getFile();
								result[0] = IDE.saveAllEditors(new IResource[] { ifile }, true);
							}
						} catch (PartInitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			} else {
				throw new MCPException("Editor does not have unsaved changes");
			}
		} else {
			throw new MCPException("editorURI could not be resolved");
		}
		return result[0];
	}

	@McpTool(name = "changeEditorText", 
			description = "Make one or more changes to an Eclipse text editor", 
			annotations = @McpTool.McpAnnotations(
					title = "Change Editor Text"))
	public boolean changeEditorText(
			@McpToolParam(description = "Open Eclipse editor URI") 
			String editorURI,
			@McpToolParam(description = "One or more text replacements to be applied in order") 
			TextReplacement[] replacements) {

		EditorAdapter adapter = new EditorAdapter();
		final IEditorReference reference = adapter.uriToEclipseObject(editorURI);
		boolean[] result = new boolean[] { false };

		// TODO apply changes in reverse order
		Arrays.sort(replacements, new Comparator<TextReplacement>() {
			@Override
			public int compare(TextReplacement o1, TextReplacement o2) {
				return o2.offset - o1.offset;
			}

		});

		if (reference != null) {
			IEditorPart part = reference.getEditor(true);
			if (part instanceof ITextEditor) {
				Activator.getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						ITextEditor textEditor = (ITextEditor) part;
						if (textEditor.isEditable()) {
							IDocument document = textEditor.getDocumentProvider()
									.getDocument(textEditor.getEditorInput());

							IRewriteTarget rewriteTarget = textEditor.getAdapter(IRewriteTarget.class);
							if (rewriteTarget != null) {
								rewriteTarget.beginCompoundChange();
							}

							try {
								for (TextReplacement replacement : replacements) {
									document.replace(replacement.offset, replacement.length, replacement.text);
								}
								result[0] = true;
							} catch (BadLocationException ble) {
								throw new MCPException(ble);
							} finally {
								if (rewriteTarget != null) {
									rewriteTarget.endCompoundChange();
								}
							}
						} else {
							throw new MCPException("The text editor is in read-only mode");
						}
					}
				});
			} else {
				throw new MCPException("Editor is not a text editor");
			}
		} else {
			throw new MCPException("editorURI could not be resolved");
		}
		return result[0];
	}

//  
//     

	@McpTool(name = "listProblems", 
			description = "list Eclipse IDE compilation and configuration problems", 
			annotations = @McpTool.McpAnnotations(
				title = "List Problems"))
	public Problems listProblems(
			@McpToolParam(
				description = "Eclipse workspace file or editor URI")
				String resourceURI,
			@McpToolParam(
				description = "One of ERROR, INFO or WARNING. Default i", 
				required = false) 
				String severity) {

		Object resource = null;
		if (resourceURI == null || resourceURI.isEmpty()) {
			resource = ResourcesPlugin.getWorkspace().getRoot();
		} else {
			resource = Activator.getDefault().getEclipseResource(resourceURI);
		}

		Integer markerSeverity = null;
		if (severity != null && !severity.isEmpty()) {
			if (severity.equals("ERROR")) {
				markerSeverity = IMarker.SEVERITY_ERROR;
			} else if (severity.equals("WARNING")) {
				markerSeverity = IMarker.SEVERITY_WARNING;
			} else if (severity.equals("INFO")) {
				markerSeverity = IMarker.SEVERITY_INFO;
			} else {
				throw new MCPException("Severity was not ERROR, WARNING or INFO");
			}
		} else {
			markerSeverity = IMarker.SEVERITY_ERROR;
		}

		if (resource instanceof IResource) {
			return new Problems((IResource) resource, markerSeverity);
		} else if (resource instanceof IEditorReference) {
			IEditorPart part = ((IEditorReference) resource).getEditor(true);
			if (part instanceof ITextEditor) {
				return new Problems((ITextEditor) part);
			}
		}

		throw new MCPException("The resource URI could not be resolved");

	}

	@McpTool (name = "listTasks", 
			description = "list codebase locations of tasks including TODO comments", 
			annotations = @McpTool.McpAnnotations(title = "List Tasks"))
	public Tasks listTasks(
			@McpToolParam(description = "Eclipse workspace file or editor URI", 
			required = false) 
			String resourceURI) {

		Object resource = null;
		if (resourceURI == null || resourceURI.isEmpty()) {
			resource = ResourcesPlugin.getWorkspace().getRoot();
		} else {
			resource = Activator.getDefault().getEclipseResource(resourceURI);
		}

		if (resource instanceof IResource) {
			return new Tasks((IResource) resource);
		} else if (resource instanceof IEditorReference) {
			IEditorPart part = ((IEditorReference) resource).getEditor(true);
			if (part == null) {
				throw new MCPException("Unable to initialize editor");
			} else if (part instanceof ITextEditor) {
				return new Tasks((ITextEditor) part);
			}
		} else if (resource == null) {
			throw new MCPException("URI did not resolve to file or editor");
		} else {
			throw new MCPException("Cound not resolve URI");
		}

		throw new MCPException("The resource URI could not be resolved");

	}

}