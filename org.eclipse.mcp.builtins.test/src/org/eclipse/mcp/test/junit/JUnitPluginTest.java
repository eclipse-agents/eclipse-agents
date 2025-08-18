package org.eclipse.mcp.test.junit;

import java.io.ByteArrayInputStream;
import java.time.Duration;
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
import org.eclipse.mcp.builtins.BuiltinFactoryProvider;
import org.eclipse.mcp.factory.IFactory;
import org.eclipse.mcp.internal.ManagedServer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ClientCapabilities;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.InitializeResult;
import io.modelcontextprotocol.spec.McpSchema.ListResourceTemplatesResult;
import io.modelcontextprotocol.spec.McpSchema.ListToolsResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import junit.framework.TestCase;
import junit.framework.TestSuite;

@RunWith(AllTests.class)
public final class JUnitPluginTest {

	/**
	 * 
	 */
	public static TestSuite suite() {
		TestSuite suite = new TestSuite();

		ManagedServer server = new ManagedServer("junit", "junit", 3028, new IFactory[] {
				new BuiltinFactoryProvider()
		});
		
		// Create a sync client with custom configuration
		HttpClientSseClientTransport transport = new HttpClientSseClientTransport("http://localhost:3028/sse");
		final McpSyncClient client = McpClient.sync(transport)
		    .requestTimeout(Duration.ofSeconds(10))
		    .capabilities(ClientCapabilities.builder()
//		        .roots(true)      // Enable roots capability
//		        .sampling()       // Enable sampling capability
//		        .elicitation()
		        .build())
//		    .sampling(request -> new CreateMessageResult(response))
//		    .elicitation(null)
		    .build();
	
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
				
			    if (!file.exists()) {
			    	String content = "public class HelloWorld {\n" +
							"    public static void main(String[] args) {\n"  +
							"        System.out.println(\"Hello, World!\");\n"  +
							"    }\n" +
							"}\n";
			    	byte[] bytes = content.getBytes();
			    	
			    	ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			        file.create(stream, true, null);
			        stream.close();
			    }
				
				project.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
				
				
				
				System.out.println(file.exists());
				
				
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						try {
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							IEditorPart editor = IDE.openEditor(page, file, true);
							if (editor instanceof ITextEditor) {
								ITextEditor textEditor= (ITextEditor) editor;
								textEditor.selectAndReveal(7, 5);
							}
							Map attr = new HashMap();
							attr.put(IMarker.MESSAGE, "There is a problem");
							attr.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
							
							attr.put(IMarker.CHAR_START, 7);
							attr.put(IMarker.CHAR_END, 12);
							attr.put(IMarker.LINE_NUMBER, 1);

							file.createMarker(IMarker.PROBLEM, attr);
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

		// Test Tool
		suite.addTest(new TestCase("Start Server") {
			@Override
			protected void runTest() throws Throwable {
				server.start();
			}
		});
		
		suite.addTest(new TestCase("Initialize Client") {
			@Override
			protected void runTest() throws Throwable {
				InitializeResult result = client.initialize();
				
				System.out.println(result.toString());
				System.out.println();
			}
		});
		
		suite.addTest(new TestCase("List Templates") {
			@Override
			protected void runTest() throws Throwable {
				ListResourceTemplatesResult templates = client.listResourceTemplates();
				
				System.out.println(templates);
				System.out.println();
			}
		});
		
		suite.addTest(new TestCase("List Tools") {
			@Override
			protected void runTest() throws Throwable {
				ListToolsResult tools = client.listTools();
				
				System.out.println(tools);
				System.out.println();
			}
		});
		
		CallToolResult[] toolResult = new CallToolResult[3];
		
		suite.addTest(new TestCase("Call Current Selection") {
			@Override
			protected void runTest() throws Throwable {
				
				toolResult[0] = client.callTool(
					    new CallToolRequest("currentSelection", "{}"));
				
				Content content = toolResult[0].content().get(0);
				String result = ((TextContent)content).text();
				System.out.println(result);

			}
		});
		
		
		suite.addTest(new TestCase("Call Tool") {
			@Override
			protected void runTest() throws Throwable {
				toolResult[1] = client.callTool(
					    new CallToolRequest("listEditors",
					        "{}"));
				
				Content content = toolResult[1].content().get(0);
				String result = ((TextContent)content).text();
				System.out.println(result);
			}
		});
		
		suite.addTest(new TestCase("Call Complex Tool") {
			@Override
			protected void runTest() throws Throwable {
				toolResult[2] = client.callTool(
					    new CallToolRequest("listConsoles",
					        "{}"));
				
				Content content = toolResult[2].content().get(0);
				String result = ((TextContent)content).text();
				System.out.println(result);
			}
		});
		
		suite.addTest(new TestCase("Client Disconnect") {
			@Override
			protected void runTest() throws Throwable {
//				Thread.sleep(90000);
				boolean close = client.closeGracefully();
				System.out.println(close);
			}
		});
		
		
		suite.addTest(new TestCase("Stop Server") {
			@Override
			protected void runTest() throws Throwable {
				server.stop();
			}
		});
		

		return suite;
	}
	
	public static void addTestMapEquals(TestSuite suite, CallToolResult[] toolResult, int i, Map<String, Object> args, String var) {
		suite.addTest(new TestCase("Test Call Tool Result: " + var) {
			@Override
			protected void runTest() throws Throwable {

				Object expected = args.get(var);
				String received = ((TextContent)toolResult[0].content().get(i)).text();
				
				if (expected instanceof String[]) {
					expected = Arrays.toString((String[])expected);
				} else if (expected instanceof Integer[]) {
					expected = Arrays.toString((Integer[])expected);
				}

				System.out.print("'" + received + "' == '" + expected + "' :: for " + var);

				
				Assert.assertEquals(var, "\n" + expected.toString(), received.toString());
			}
		});
	}
	
	
}
