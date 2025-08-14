package org.eclipse.mcp.test.junit;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mcp.IMCPResourceTemplateFactory;
import org.eclipse.mcp.IMCPToolFactory;
import org.eclipse.mcp.test.junit.plugin.extension.MCPFactory;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import io.modelcontextprotocol.server.McpServerFeatures.SyncCompletionSpecification;
import io.modelcontextprotocol.server.McpServerFeatures.SyncResourceSpecification;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ResourceTemplate;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import junit.framework.TestCase;
import junit.framework.TestSuite;

@RunWith(AllTests.class)
public final class MCPFactoryTest {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		MCPFactory factory = new MCPFactory();
		addFactoryTests(suite, factory);
		addResourceTemplateTests(suite, factory);
		return suite;
		
	}
	
	public static void addFactoryTests(TestSuite suite, MCPFactory factory) {
		
		IMCPToolFactory[] toolFactories = factory.createToolFactories();
		addTestEquals(suite, "toolFactory[0].id", toolFactories[0].getId(), "junit.MCPToolFactory.helloWorld");
		
		Tool tool = toolFactories[0].createTool();
		
		
		addTestEquals(suite, "tool.name", tool.name(), "test-hello-world");
		addTestEquals(suite, "tool.description", tool.description(), "Greets user with a hello");
		
		addTestEquals(suite, "tool.inputSchema.type", tool.inputSchema().type(), "object");
		
		String arg = "b1";
		addTestEquals(suite, "tool.inputSchema." + arg + ".type", ((Map)tool.inputSchema().properties().get(arg)).get("type"), "boolean");
		addTestEquals(suite, "tool.inputSchema." + arg + ".description", ((Map)tool.inputSchema().properties().get(arg)).get("description"), "boolean");

		arg = "c1";
		addTestEquals(suite, "tool.inputSchema." + arg + ".type", ((Map)tool.inputSchema().properties().get(arg)).get("type"), "string");
		addTestEquals(suite, "tool.inputSchema." + arg + ".description", ((Map)tool.inputSchema().properties().get(arg)).get("description"), "character");

		arg = "s1";
		addTestEquals(suite, "tool.inputSchema." + arg + ".type", ((Map)tool.inputSchema().properties().get(arg)).get("type"), "string");
		addTestEquals(suite, "tool.inputSchema." + arg + ".description", ((Map)tool.inputSchema().properties().get(arg)).get("description"), "string");

		arg = "d1";
		addTestEquals(suite, "tool.inputSchema." + arg + ".type", ((Map)tool.inputSchema().properties().get(arg)).get("type"), "number");
		addTestEquals(suite, "tool.inputSchema." + arg + ".description", ((Map)tool.inputSchema().properties().get(arg)).get("description"), "double");

		arg = "f1";
		addTestEquals(suite, "tool.inputSchema." + arg + ".type", ((Map)tool.inputSchema().properties().get(arg)).get("type"), "number");
		addTestEquals(suite, "tool.inputSchema." + arg + ".description", ((Map)tool.inputSchema().properties().get(arg)).get("description"), "float");

		arg = "i1";
		addTestEquals(suite, "tool.inputSchema." + arg + ".type", ((Map)tool.inputSchema().properties().get(arg)).get("type"), "integer");
		addTestEquals(suite, "tool.inputSchema." + arg + ".description", ((Map)tool.inputSchema().properties().get(arg)).get("description"), "integer");

		arg = "l1";
		addTestEquals(suite, "tool.inputSchema." + arg + ".type", ((Map)tool.inputSchema().properties().get(arg)).get("type"), "integer");
		addTestEquals(suite, "tool.inputSchema." + arg + ".description", ((Map)tool.inputSchema().properties().get(arg)).get("description"), "long");


		arg = "sh1";
		addTestEquals(suite, "tool.inputSchema." + arg + ".type", ((Map)tool.inputSchema().properties().get(arg)).get("type"), "integer");
		addTestEquals(suite, "tool.inputSchema." + arg + ".description", ((Map)tool.inputSchema().properties().get(arg)).get("description"), "short");


		arg = "as1";
		addTestEquals(suite, "tool.inputSchema." + arg + ".type", ((Map)tool.inputSchema().properties().get(arg)).get("type"), "array");
		addTestEquals(suite, "tool.inputSchema." + arg + ".description", ((Map)tool.inputSchema().properties().get(arg)).get("description"), "string array");
		addTestEquals(suite, "tool.inputSchema." + arg + ".item.type", ((Map)((Map)tool.inputSchema().properties().get(arg)).get("items")).get("type"), "string");

		
		arg = "ai1";
		addTestEquals(suite, "tool.inputSchema." + arg + ".type", ((Map)tool.inputSchema().properties().get(arg)).get("type"), "array");
		addTestEquals(suite, "tool.inputSchema." + arg + ".description", ((Map)tool.inputSchema().properties().get(arg)).get("description"), "int array");
		addTestEquals(suite, "tool.inputSchema." + arg + ".item.type", ((Map)((Map)tool.inputSchema().properties().get(arg)).get("items")).get("type"), "integer");


//		addTestEquals(suite, "tool.outputSchema", tool.outputSchema(), "junit.MCPToolFactory.helloWorld");
		
		addTestEquals(suite, "tool.annotation.title", tool.annotations().title(), "");
		addTestEquals(suite, "tool.annotation.destructiveHint", tool.annotations().destructiveHint(), false);
		addTestEquals(suite, "tool.annotation.idempotentHint", tool.annotations().idempotentHint(), false);
		addTestEquals(suite, "tool.annotation.openWorldHint", tool.annotations().openWorldHint(), false);
		addTestEquals(suite, "tool.annotation.readOnlyHint", tool.annotations().readOnlyHint(), false);
		addTestEquals(suite, "tool.annotation.returnDirect", tool.annotations().returnDirect(), false);
		
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("b1", Boolean.TRUE);
		args.put("c1", Character.valueOf('a'));
		args.put("s1", "Hello");
		args.put("d1", Double.parseDouble("2.3"));
		args.put("f1", Float.valueOf("3.4"));
		args.put("i1", Integer.parseInt("1234"));
		args.put("l1", Long.valueOf(1234));
		args.put("sh1", Short.parseShort("1234"));
		args.put("as1", new String[] { "jeremy", "flicker" });
		args.put("ai1", new Integer[] { 1234, 2345 });
		

		SyncToolSpecification spec = toolFactories[0].createSpec(tool);
		CallToolRequest request = new CallToolRequest(tool.name(), args);
		CallToolResult result = spec.callHandler().apply((McpSyncServerExchange)null, request);
		
		arg = "b1";
		addTestEquals(suite, "result." + arg, ((TextContent)result.content().get(0)).text(), "\n" + args.get(arg));
		
		arg = "c1";
		addTestEquals(suite, "result." + arg, ((TextContent)result.content().get(1)).text(), "\n" + args.get(arg));
		
		arg = "s1";
		addTestEquals(suite, "result." + arg, ((TextContent)result.content().get(2)).text(), "\n" + args.get(arg));
		
		arg = "d1";
		addTestEquals(suite, "result." + arg, ((TextContent)result.content().get(3)).text(), "\n" + args.get(arg));
		
		arg = "f1";
		addTestEquals(suite, "result." + arg, ((TextContent)result.content().get(4)).text(), "\n" + args.get(arg));
		
		arg = "i1";
		addTestEquals(suite, "result." + arg, ((TextContent)result.content().get(5)).text(), "\n" + args.get(arg));
		
		arg = "l1";
		addTestEquals(suite, "result." + arg, ((TextContent)result.content().get(6)).text(), "\n" + args.get(arg));
		
		arg = "sh1";
		addTestEquals(suite, "result." + arg, ((TextContent)result.content().get(7)).text(), "\n" + args.get(arg));
		
		arg = "as1";
		addTestEquals(suite, "result." + arg, ((TextContent)result.content().get(8)).text(), "\n" + Arrays.toString((String[])args.get(arg)));
		
		arg = "ai1";
		addTestEquals(suite, "result." + arg, ((TextContent)result.content().get(9)).text(), "\n" + Arrays.toString((Integer[])args.get(arg)));
	
	}
	
	public static void addResourceTemplateTests(TestSuite suite, MCPFactory factory) {
		
		IMCPResourceTemplateFactory templateFactory = factory.createResourceTemplateFactories()[0];
		
		ResourceTemplate[] templates = templateFactory.createResourceTemplates();
		addTestTrue(suite, "2 templates", templates.length == 2);
		
		ResourceTemplate template1 = templates[0];

		SyncCompletionSpecification completion = templateFactory.createCompletionSpecification(template1);
		addTestEquals(suite, completion.getClass().getSimpleName(), template1.uriTemplate(), completion.referenceKey().identifier());

		SyncResourceSpecification resource = templateFactory.getResourceTemplateSpecification(template1);	
		addTestEquals(suite, resource.getClass().getSimpleName(), template1.uriTemplate(), resource.resource().uri());
		
		ResourceTemplate template2 = templates[1];
		
		completion = templateFactory.createCompletionSpecification(template2);
		addTestEquals(suite, completion.getClass().getSimpleName(), template2.uriTemplate(), completion.referenceKey().identifier());
		
		resource = templateFactory.getResourceTemplateSpecification(template2);	
		addTestEquals(suite, resource.getClass().getSimpleName(), template2.uriTemplate(), resource.resource().uri());

	}
		
	
	public static void addTestEquals(TestSuite suite, String message, Object left, Object right) {
		suite.addTest(new TestCase(message) {
			@Override
			protected void runTest() throws Throwable {
				System.out.println(message + ":: '" + left + "' == '" + right + "'");
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
