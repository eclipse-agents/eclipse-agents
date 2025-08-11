package org.eclipse.mcp.test.junit.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.mcp.IElementProperties;
import org.eclipse.mcp.IMCPResourceController;
import org.eclipse.mcp.IMCPResourceFactory;
import org.eclipse.mcp.IMCPTool;
import org.eclipse.mcp.internal.ExtensionManager;
import org.eclipse.mcp.internal.ExtensionManager.ResourceController;
import org.eclipse.mcp.internal.ExtensionManager.Tool;
import org.eclipse.mcp.test.junit.plugin.extension.MCPTool;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
		addTestEquals(suite, "mcp server.getDefaultPort", server.getDefaultPort(), "10203");


		// Test Tool
		for (Tool tool: server.getTools()) {
			if (tool.getId().equals(toolId)) {
				
				foundTool = true;
				
				addTestEquals(suite, "mcp tool.id", tool.getId(), toolId);
				addTestEquals(suite, "mcp tool.name", tool.getName(), "tool.name");
				addTestEquals(suite, "mcp tool.description", tool.getDescription(), "tool.description");
				addTestEquals(suite, "mcp tool.category", tool.getCategory(), "extension.category");
				addTestTrue(suite, "mcp tool.implementation", tool.getImplementation() instanceof IMCPTool);
				
				
				// Validate JSON Schema
				String schema = tool.getSchema();
				JsonObject json = JsonParser.parseString(schema).getAsJsonObject();
				
				addTestEquals(suite, "mcp tool.schema.type", 
						json.get("type").getAsString(), 
						"object");
				
				addTestEquals(suite, "mcp tool.schema.properties.name.type", 
						json.get("properties").getAsJsonObject().get("name").getAsJsonObject().get("type").getAsString(),
						"string");
				addTestEquals(suite, "mcp tool.schema.required", 
						json.get("required").getAsJsonArray().get(0).getAsString(), 
						"name");
				
				// Validate Tool Implementation
				Map<String, Object> args = new HashMap<String, Object>(); 
				args.put("name", "Jeremy");
				
				IElementProperties properties = new IElementProperties() {
					@Override
					public IDialogSettings getProperties(String propertyPageId) {
						DialogSettings settings = new DialogSettings(propertyPageId);
						if (propertyPageId.equals(MCPTool.class.getCanonicalName())) {
							settings.put("name", "Flicker");
						}
						return settings;
					}

					@Override
					public void openPropertiesEditor(String selectedPageId) {
					}	
				};
				String[] response = tool.getImplementation().apply(args, properties);
				addTestEquals(suite, "mcp tool.implementation.apply.1",
						response[0],
						"Hello Jeremy");
				addTestEquals(suite, "mcp tool.implementation.apply.2",
						response[1],
						"Goodbye Flicker");
				
				
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
				
				IMCPResourceController implementation = controller.getImplementation();
				final String[] addedURI = new String[] { null };
				final String[] removedURI = new String[] { null };
				implementation.initialize(new IMCPResourceFactory() {
					@Override
					public UUID addResource(String uri, String name, String description, String mimeType) {
						addedURI[0] = uri;
						return UUID.randomUUID();
					}

					@Override
					public void removeResource(String uri) {
						removedURI[0] = uri;
					}
				});
				addTestTrue(suite, "mcp resourceController.implementation.initialize URI added", addedURI[0] != null);
				addTestEquals(suite, "mcp resourceController.implementation.initialize URI removed", addedURI[0], removedURI[0]);
				
				String[] readResource = implementation.readResource(addedURI[0]);
				addTestEquals(suite, "mcp resourceController.implementation.readResource 1", readResource[0], "Hello");
				addTestEquals(suite, "mcp resourceController.implementation.readResource 2", readResource[1], "World");
				
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
				System.out.println(left + " == " + right);
				Assert.assertEquals(message, left, right);
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
