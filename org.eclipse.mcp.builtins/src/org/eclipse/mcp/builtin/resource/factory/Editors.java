package org.eclipse.mcp.builtin.resource.factory;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.mcp.IMCPServices;
import org.eclipse.mcp.builtin.resource.templates.Templates;
import org.eclipse.mcp.internal.Tracer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.springaicommunity.mcp.provider.resource.SyncMcpResourceProvider;

import io.modelcontextprotocol.server.McpServerFeatures.SyncResourceSpecification;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.Resource;

/**
 * Synchronizes availability of one ""eclipse://editor/{name}" MCP resource for each open Eclipse editor
 */
public class Editors  {

	IMCPServices services;
	
	IWindowListener windowListener;
	IPageListener pageListener;
	IPartListener partListener;
	
	Set<String> editorNames = new HashSet<String>();
	
	IEditorPart lastActiveEditor;
	
	Set<String> resourceURIs = new HashSet<String>();
	SyncResourceSpecification editorTemplateSpec;

	public Editors() {
		super();
		
		SyncMcpResourceProvider provider = new SyncMcpResourceProvider(Arrays.asList(new Templates()));
		for (SyncResourceSpecification spec: provider.getResourceSpecifications()) {
			if (spec.resource().uri().equals("eclipse://editor/{name}")) {
				editorTemplateSpec = spec;
				break;
			}
		}
 
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
				if (part instanceof IEditorPart) {
					lastActiveEditor = (IEditorPart)part;
					if (!editorNames.contains(part.getTitle())) {
						addResource((ITextEditor)part);
					}
				}
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPart part) {}
			
			@Override
			public void partClosed(IWorkbenchPart part) {
				if (part instanceof ITextEditor) {
					removeResource((ITextEditor)part);
				}
			}
			
			@Override
			public void partDeactivated(IWorkbenchPart part) {
				// do nothing
				System.out.println("DEACTIVATE: " + part.getTitle());
			}
			
			@Override
			public void partOpened(IWorkbenchPart part) {
				if (part instanceof ITextEditor) {
					addResource((ITextEditor)part);
				}
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


	public void initialize(IMCPServices services) {
		this.services = services;
		
		editorNames.clear();
		
		for (IWorkbenchWindow window: PlatformUI.getWorkbench().getWorkbenchWindows()) {
			for (IWorkbenchPage page: window.getPages()) {
				for (IEditorReference ref: page.getEditorReferences()) {
					// get part only if its been initialized
					IEditorPart part = ref.getEditor(false);
					if (part instanceof ITextEditor) {
						addResource((ITextEditor)part);
					}
				}
			}
		}
		
	}
	
	private void addResource(ITextEditor part) {
		if (this.services != null && !editorNames.contains(part.getTitle())) {
			editorNames.add(part.getTitle());
			
			Resource resource = McpSchema.Resource.builder()
				.name(part.getTitle())
				.uri("eclipse://editor/"+ URLEncoder.encode(part.getTitle()))
				.description(part.getTitleToolTip())
				.mimeType("text/plain")
				.build();

			SyncResourceSpecification spec = new SyncResourceSpecification(resource, editorTemplateSpec.readHandler());
			services.addResource(spec);
			
			Tracer.trace().trace(Tracer.PLATFORM, "Adding Text Editor Resource: " + resource.uri());
			
		}
	}
	
	
	private void removeResource(ITextEditor part) {
		if (this.services != null && editorNames.contains(part.getTitle())) {
			editorNames.remove(part.getTitle());
			
			String uri = "eclipse://editor/" + part.getTitle();
			services.removeResource(uri);
			
			Tracer.trace().trace(Tracer.PLATFORM, "Removing Text Editor Resource: " + uri);
		}
	}
}
