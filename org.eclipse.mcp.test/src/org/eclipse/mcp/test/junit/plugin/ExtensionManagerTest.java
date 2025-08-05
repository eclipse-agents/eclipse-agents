package org.eclipse.mcp.test.junit.plugin;

import org.eclipse.mcp.IMCPResourceController;
import org.eclipse.mcp.IMCPTool;
import org.eclipse.mcp.internal.ExtensionManager;
import org.eclipse.mcp.internal.ExtensionManager.ResourceController;
import org.eclipse.mcp.internal.ExtensionManager.Tool;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

@RunWith(AllTests.class)
public final class ExtensionManagerTest {

	/**
	 * 
	 */
	public static TestSuite suite() {
		TestSuite suite = new TestSuite();

		ExtensionManager manager = new ExtensionManager();

		
		final String serverId = "org.eclipse.mcp.test.junit.plugin.extension.server";
        final String toolId = "org.eclipse.mcp.test.junit.plugin.extension.tool";
        final String resourceControllerId = "org.eclipse.mcp.test.junit.plugin.extension.controller";
        final String propertyPageId = "org.eclipse.mcp.test.junit.plugin.extension.SamplePropertyPage";
   
		boolean foundTool = false;
		boolean foundResourceController = false;


		// Test Server
		ExtensionManager.Server server = manager.getServer(serverId);
		addTestTrue(suite, "mcp server", server != null);
		addTestEquals(suite, "mcp server id", server.getId(), serverId);
		addTestEquals(suite, "mcp server.getName", server.getName(), "server.name");
		addTestEquals(suite, "mcp server.getDescription", server.getDescription(), "server.description");
		addTestEquals(suite, "mcp server.getVersion", server.getVersion(), "server.version");
		addTestEquals(suite, "mcp server.getDefaultPort", server.getDefaultPort(), "server.port");


		// Test Tool
		for (Tool tool: server.getTools()) {
			if (tool.getId().equals(toolId)) {
				
				foundTool = true;
				
				addTestEquals(suite, "mcp server id", tool.getId(), toolId);
				addTestEquals(suite, "mcp tool.name", tool.getName(), "tool.name");
				addTestEquals(suite, "mcp tool.description", tool.getDescription(), "tool.description");
				addTestEquals(suite, "mcp tool.category", tool.getCategory(), "extension.category");
				addTestTrue(suite, "mcp tool.implementation", tool.getImplementation() instanceof IMCPTool);
				addTestEquals(suite, "mcp tool.schema", tool.getSchema(), "tool.schema");
				
				addTestEquals(suite, "mcp tool.propertyEditorIds", tool.getPropertyEditorIds()[0], "org.eclipse.mcp.test.junit.plugin.extension.SamplePropertyPage");
				addTestEquals(suite, "mcp tool.propertyEditorIds length 1",tool.getPropertyEditorIds().length, 1);
			}
		}
		addTestTrue(suite, "mcp tool found", foundTool);
		
		
		// Test Controller
		for (ResourceController controller: server.getResourceControllers()) {
			if (controller.getId().equals(resourceControllerId)) {
				
				foundResourceController = true;
				
				addTestEquals(suite, "mcp resourceController.id", controller.getId(), resourceControllerId);
				addTestEquals(suite, "mcp resourceController.name", controller.getName(), "controller.name");
				addTestEquals(suite, "mcp resourceController.description", controller.getDescription(), "controller.description");
				addTestEquals(suite, "mcp resourceController.category", controller.getCategory(), "extension.category");
				addTestTrue(suite, "mcp resourceController.implementation", controller.getImplementation() instanceof IMCPResourceController);
				
				addTestEquals(suite, "mcp resourceController.propertyEditorIds", controller.getPropertyEditorIds()[0], "org.eclipse.mcp.test.junit.plugin.extension.SamplePropertyPage");
				addTestEquals(suite, "mcp resourceController.propertyEditorIds length 1",controller.getPropertyEditorIds().length, 1);foundTool = true;
			}
			
			addTestTrue(suite, "mcp tool found", foundResourceController);
		}

		return suite;
	}

	public static void addTestEquals(TestSuite suite, String message, Object left, Object right) {
		suite.addTest(new TestCase(message) {
			@Override
			protected void runTest() throws Throwable {
				Assert.assertEquals(message, left, right);
				System.out.println(left + " == " + right);
			}
		});
	}

	public static void addTestTrue(TestSuite suite, String message, boolean test) {
		suite.addTest(new TestCase(message) {
			@Override
			protected void runTest() throws Throwable {
				Assert.assertTrue(message, test);
			}
		});
	}
}
