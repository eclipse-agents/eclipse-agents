package org.eclipse.mcp.builtin.resource.factory;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.mcp.IResourceController;
import org.eclipse.mcp.factory.IResourceFactory;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import io.modelcontextprotocol.spec.McpSchema;

/**
 * Maintains an MCP resource for each open Eclipse Text Editor
 */
public class Editors implements IResourceFactory {

	IResourceController controller;
	
	IWindowListener windowListener;
	IPageListener pageListener;
	IPartListener partListener;
	
	Set<String> openURIs = new HashSet<String>();

	public Editors() {
		super();
	}

	@Override
	public void initialize(IResourceController controller) {
		this.controller = controller;
		
		windowListener = new IWindowListener() {
			@Override
			public void windowActivated(IWorkbenchWindow arg0) {}
			@Override
			public void windowClosed(IWorkbenchWindow arg0) {}
			@Override
			public void windowDeactivated(IWorkbenchWindow arg0) {}
			@Override
			public void windowOpened(IWorkbenchWindow window) {
				window.addPageListener(pageListener);
				for (IWorkbenchPage page: window.getPages()) {
					page.addPartListener(partListener);
				}
			}
		};
		pageListener = new IPageListener() {
			@Override
			public void pageActivated(IWorkbenchPage arg0) {}
			@Override
			public void pageClosed(IWorkbenchPage arg0) {}
			@Override
			public void pageOpened(IWorkbenchPage page) {
				page.addPartListener(partListener);
			}
			
		};
		partListener = new IPartListener() {
			@Override
			public void partActivated(IWorkbenchPart part) {
				addActiveResource(part);
			}
			@Override
			public void partBroughtToTop(IWorkbenchPart part) {}
			@Override
			public void partClosed(IWorkbenchPart part) {
				removeResource(part);
			}
			@Override
			public void partDeactivated(IWorkbenchPart part) {
				removeActiveResource(part);
			}
			@Override
			public void partOpened(IWorkbenchPart part) {
				addResource(part);
			}
			
		};
		
		PlatformUI.getWorkbench().addWindowListener(windowListener);
		for (IWorkbenchWindow window: PlatformUI.getWorkbench().getWorkbenchWindows()) {
			window.addPageListener(pageListener);
			for (IWorkbenchPage page: window.getPages()) {
				page.addPartListener(partListener);
			}
		}
	}
	
	private void addResource(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			IEditorInput input = ((IEditorPart) part).getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput) input).getFile();
				String uri = file.getLocationURI().toString();
				if (!openURIs.contains(uri)) {
				
					controller.addResource(McpSchema.Resource.builder()
							.name(part.getTitle())
							.uri("eclipse://editor/"+ part.getTitle())
							.description(part.getTitleToolTip())
							.mimeType("text/plain")
							.build());
					openURIs.add(uri);
				}
			}
		}
	}
	
	private void removeResource(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			IEditorInput input = ((IEditorPart) part).getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput) input).getFile();
				String uri = file.getLocationURI().toString();
				if (openURIs.contains(uri)) {
					controller.removeResource(uri);
					openURIs.remove(uri);
				}
			}
		}
	}
	
    private void addActiveResource(IWorkbenchPart part) {
		
	}
	
	private void removeActiveResource(IWorkbenchPart part) {
		
	}

	@Override
	public String[] readResource(String url) {
		//TODO {
		// should be handled by the editor template template
		return null;
	}
}
