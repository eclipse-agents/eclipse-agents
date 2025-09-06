package org.eclipse.mcp.test.plugin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mcp.IFactoryProvider;
import org.eclipse.mcp.internal.MCPServer;
import org.eclipse.mcp.platform.FactoryProvider;
import org.eclipse.mcp.platform.resource.ConsoleAdapter;
import org.eclipse.mcp.platform.resource.EditorAdapter;
import org.eclipse.mcp.platform.resource.WorkspaceResourceAdapter;
import org.eclipse.mcp.resource.IResourceTemplate;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public final class ResourceAdaptersTest {

	MCPServer server;
	String content = "public class HelloWorld {\n" + "    public static void main(String[] args) {\n"
			+ "        System.out.println(\"Hello, World!\");\n" + "    }\n" + "}\n";

	@BeforeAll
	public void setup() throws CoreException, IOException {
		server = new MCPServer("junit", "junit", 3028, new IFactoryProvider[] { new FactoryProvider() });

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		final IProject project = workspace.getRoot().getProject("Project");
		try {
			IWorkspaceRunnable create = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					project.create(null, null);
					project.open(null);
				}
			};
			workspace.run(create, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		IFile file = project.getFile("HelloWorld.java");

		final File f = new File(file.getFullPath().toOSString());
		System.out.println(f.toURI());
//				 file.getFullPath()

		if (!file.exists()) {
			byte[] bytes = content.getBytes();

			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			file.create(stream, true, null);
			stream.close();
		}
		
		project.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());

		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IWorkbenchPart part = page.getActivePart();
					part.dispose();

					IEditorPart editor = IDE.openEditor(page, file, true);
					if (editor instanceof ITextEditor) {
						ITextEditor textEditor = (ITextEditor) editor;
						textEditor.selectAndReveal(7, 5);
						page.activate(textEditor);
					}
					Map attr = new HashMap();
					attr.put(IMarker.MESSAGE, "There is a problem");
					attr.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);

					attr.put(IMarker.CHAR_START, 7);
					attr.put(IMarker.CHAR_END, 12);
					attr.put(IMarker.LINE_NUMBER, 1);

					file.createMarker(IMarker.PROBLEM, attr);

					page.getActivePart();
				} catch (PartInitException e) {
					e.printStackTrace();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		server.start();
	}

	// Templates use URIEncoding on content of substitution variables
	String console = "eclipse://console/z/OS";
	String consoleEscaped = "eclipse://console/z%2FOS";
	String editor = "eclipse://editor/HelloWorld.java";

	final String relativeFile = "file://workspace/Project/HelloWorld.java";
	final String relativeFileEscaped = "file://workspace/Project%2FHelloWorld.java";
	final String absoluteFile1 = "file:/Users/jflicke/junit-workspace/Project/HelloWorld.java";
	final String absoluteFile3 = "file:///Users/jflicke/junit-workspace/Project/HelloWorld.java";

	final String relativeProject = "file://workspace/Project";
	final String absoluteProject1 = "file:/Users/jflicke/junit-workspace/Project";
	final String absoluteProject3 = "file:///Users/jflicke/junit-workspace/Project";
	
	final String relativeWorkspace = "file://workspace";
	final String absoluteWorkspace1 = "file:/Users/jflicke/junit-workspace";
	final String absoluteWorkspace3 = "file:///Users/jflicke/junit-workspace";
	
	String escape = "%2F";

	@Test
	public void testConsole() {
		ConsoleAdapter adapter = (ConsoleAdapter) server.getResourceTemplate(consoleEscaped);
		Assert.assertNotNull(adapter);
		testEquals(adapter.toUri(), consoleEscaped);
		IConsole console = adapter.getModel();
		Assert.assertNotNull(console);
		logResourceTemplate(adapter);
	}

	@Test
	public void testEditor() {

		EditorAdapter adapter = (EditorAdapter) server.getResourceTemplate(editor);
		logResourceTemplate(adapter);

		testEclipseResource(editor, "org.eclipse.ui.internal.EditorReference");
		testContentEquals(editor, content);
	}

	@Test
	@DisplayName(relativeFile)
	public void testFiles1() {
		WorkspaceResourceAdapter adapter = (WorkspaceResourceAdapter) 
				server.getResourceTemplate(relativeFile);
		Assert.assertEquals(absoluteFile1, adapter.toUri());
	}
	
	@Test
	@DisplayName(relativeFileEscaped)
	public void testFiles2() {
		WorkspaceResourceAdapter adapter = (WorkspaceResourceAdapter) 
				server.getResourceTemplate(relativeFileEscaped);
		Assert.assertEquals(absoluteFile1, adapter.toUri());
	}
	
	@Test
	@DisplayName(absoluteFile1)
	public void testFiles3() {
		WorkspaceResourceAdapter adapter = (WorkspaceResourceAdapter) 
				server.getResourceTemplate(absoluteFile1);
		Assert.assertEquals(absoluteFile1, adapter.toUri());
	}
	
	@Test
	@DisplayName(absoluteFile3)
	public void testFiles4() {
		WorkspaceResourceAdapter adapter = (WorkspaceResourceAdapter) 
				server.getResourceTemplate(absoluteFile3);
		Assert.assertEquals(absoluteFile1, adapter.toUri());
	}
	
	@Test
	@DisplayName(relativeProject)
	public void testFolder1() {
		WorkspaceResourceAdapter adapter = (WorkspaceResourceAdapter) 
				server.getResourceTemplate(relativeProject);
		Assert.assertEquals(absoluteProject1, adapter.toUri());
	}
	
	@Test
	@DisplayName(absoluteProject1)
	public void testFolder2() {
		WorkspaceResourceAdapter adapter = (WorkspaceResourceAdapter) 
				server.getResourceTemplate(absoluteProject1);
		Assert.assertEquals(absoluteProject1, adapter.toUri());
	}
	
	@Test
	@DisplayName(absoluteProject3)
	public void testFolder3() {
		WorkspaceResourceAdapter adapter = (WorkspaceResourceAdapter) 
				server.getResourceTemplate(absoluteProject3);
		Assert.assertEquals(absoluteProject1, adapter.toUri());
	}
	
	@Test
	@DisplayName(relativeWorkspace)
	public void testFolder4() {
		WorkspaceResourceAdapter adapter = (WorkspaceResourceAdapter) 
				server.getResourceTemplate(relativeWorkspace);
		Assert.assertEquals(absoluteWorkspace1, adapter.toUri());
	}
	
	@Test
	@DisplayName(absoluteWorkspace1)
	public void testFolder5() {
		WorkspaceResourceAdapter adapter = (WorkspaceResourceAdapter) 
				server.getResourceTemplate(absoluteWorkspace1);
		Assert.assertEquals(absoluteWorkspace1, adapter.toUri());
	}

	public void testEclipseResource(String uri, String className) {
		Object eclipseResource = server.getResourceTemplate(uri).getModel();
		System.out.println(uri + ": " + eclipseResource);
		Assert.assertEquals(eclipseResource.getClass().getCanonicalName(), className);
	}

	public void logResourceTemplate(IResourceTemplate<?, ?> template) {
		System.out.println("\n>>>>>>>");
		System.out.println(template.getClass().getCanonicalName());
		System.out.println("templates:");
		for (String s : template.getTemplates()) {
			System.out.println("\t" + s);
		}
		System.out.println("Model: " + template.getModel().getClass().getCanonicalName());
		System.out.println("JSON:");
		System.out.println("\t" + template.toJson());
		System.out.println("Content:");
		System.out.println(template.toContent());
		System.out.println("<<<<<<<<");
	}

	public void testContentEquals(String uri, String content) {
		String resourceContent = server.getResourceTemplate(uri).toContent();
		System.out.println(uri + ": " + resourceContent);
		Assert.assertEquals(uri, resourceContent.trim(), content.trim());
	}

	public void testEquals(String left, String right) {
		System.out.println(left + " == " + right);
		Assert.assertEquals(left, right);
	}

	public void testArrayEquals(String message, String[] left, String[] right) {
		System.out.println(message + ":: " + Arrays.toString(left) + " == " + Arrays.toString(right));
		Assert.assertArrayEquals(message, left, right);
	}

}
