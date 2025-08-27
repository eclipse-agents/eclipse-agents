package org.eclipse.mcp.builtins.test.plugin;

import java.io.ByteArrayInputStream;
import java.io.File;
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
import org.eclipse.mcp.Activator;
import org.eclipse.mcp.builtins.BuiltinFactoryProvider;
import org.eclipse.mcp.factory.IFactoryProvider;
import org.eclipse.mcp.internal.MCPServer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

@RunWith(AllTests.class)
public final class RouterTest {
	
	
	final static MCPServer server = new MCPServer("junit", "junit", 3028, new IFactoryProvider[] {
			new BuiltinFactoryProvider()
	});

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
	
		

		suite.addTest(new TestCase("Don't run in UI Thread") {
			@Override
			protected void runTest() throws Throwable {
				Assert.assertTrue("Dont run test in UI thread", !Thread.currentThread().equals((Display.getDefault().getThread())));
			}
		});
		
		String content = "public class HelloWorld {\n" +
				"    public static void main(String[] args) {\n"  +
				"        System.out.println(\"Hello, World!\");\n"  +
				"    }\n" +
				"}\n";
		
		suite.addTest(new TestCase("Set-up project") {
			@Override
			protected void runTest() throws Throwable {
				
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
				 
				 File f = new File(file.getFullPath().toOSString());
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
								ITextEditor textEditor= (ITextEditor) editor;
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
			}
		});
		

		suite.addTest(new TestCase("Start Server") {
			@Override
			protected void runTest() throws Throwable {
				server.start();
			}
		});
		
		String console = "eclipse://console/z/OS";
		String consoleEscaped = "eclipse://console/z%2FOS";
		String editor = "eclipse://editor/HelloWorld.java";
		String relativeFile = "file://workspace/Project/HelloWorld.java";
		String relativeFileEscaped = "file://workspace/Project%2FHelloWorld.java";
		String absoluteFile = "file:///Users/jflicke/junit-workspace/Project/HelloWorld.java";
		String escape = "%2F";
		
		
//		testEclipseResource(suite, console, "com.ibm.cics.zos.core.ui.ZOSConsole");
		testEclipseResource(suite, consoleEscaped, "com.ibm.cics.zos.core.ui.ZOSConsole");
		testEclipseResource(suite, editor, "org.eclipse.ui.internal.EditorReference");
//		testEclipseResource(suite, relativeFile, "org.eclipse.core.internal.resources.File");
		testEclipseResource(suite, relativeFileEscaped, "org.eclipse.core.internal.resources.File");
//		testEclipseResource(suite, absoluteFile,"java.io.File");

		
//		testContentEquals(suite, console, null);
		testContentEquals(suite, consoleEscaped, null);
		testContentEquals(suite, editor, content);
//		testContentEquals(suite, relativeFile, content);
		testContentEquals(suite, relativeFileEscaped, content);
//		testContentEquals(suite, absoluteFile, content);
		
		return suite;
	}
		
		
	public static void testEclipseResource(TestSuite suite, String uri, String className) {
		suite.addTest(new TestCase("getEclipseResource: " + uri) {
			@Override
			protected void runTest() throws Throwable {
				Object eclipseResource = server.getResourceAdapter(uri).uriToEclipseObject(uri);
				System.out.println(uri + ": " + eclipseResource);
				Assert.assertEquals(eclipseResource.getClass().getCanonicalName(), className);

			}
		});
	}
	
	public static void testContentEquals(TestSuite suite, String uri, String content) {
		suite.addTest(new TestCase("getResourceContent: " + uri) {
			@Override
			protected void runTest() throws Throwable {
				String resourceContent = server.getResourceAdapter(uri).uriToResourceContent(uri);
				System.out.println(uri + ": " + resourceContent);
				Assert.assertEquals(uri, resourceContent.trim(), content.trim());
			}
		});
	}
	
	public static void testArrayEquals(String message, String[] left, String[] right) {
		System.out.println(message + ":: " + Arrays.toString(left) + " == " + Arrays.toString(right));
		Assert.assertArrayEquals(message, left, right);
	}
	
}
