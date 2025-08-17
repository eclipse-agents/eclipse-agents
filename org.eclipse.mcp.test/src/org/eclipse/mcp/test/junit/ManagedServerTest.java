package org.eclipse.mcp.test.junit;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mcp.factory.IFactory;
import org.eclipse.mcp.internal.ManagedServer;
import org.eclipse.mcp.test.junit.plugin.extension.FactoryProvider;
import org.eclipse.mcp.test.junit.plugin.extension.json.Address;
import org.eclipse.mcp.test.junit.plugin.extension.json.Person;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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
public final class ManagedServerTest {

	/**
	 * 
	 */
	public static TestSuite suite() {
		TestSuite suite = new TestSuite();

		ManagedServer server = new ManagedServer("junit", "junit", 1834, new IFactory[] { new FactoryProvider() });
		
		// Create a sync client with custom configuration
		HttpClientSseClientTransport transport = new HttpClientSseClientTransport("http://localhost:1834/sse");
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
		
		CallToolResult[] toolResult = new CallToolResult[] { null };
		
		suite.addTest(new TestCase("Call Basic Tool") {
			@Override
			protected void runTest() throws Throwable {
				
				toolResult[0] = client.callTool(
					    new CallToolRequest("test-hello-world-basic", "{}"));
				
				Content content = toolResult[0].content().get(0);
				String result = ((TextContent)content).text();
				
				Assert.assertEquals("Validate basic response: name", result, "Hello");

			}
		});
		
		
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
		
		suite.addTest(new TestCase("Call Tool") {
			@Override
			protected void runTest() throws Throwable {
				toolResult[0] = client.callTool(
					    new CallToolRequest("test-hello-world",
					        args));
			}
		});
		
		addTestMapEquals(suite, toolResult, 0, args, "b1");
		addTestMapEquals(suite, toolResult, 1, args, "c1");
		addTestMapEquals(suite, toolResult, 2, args, "s1");
		addTestMapEquals(suite, toolResult, 3, args, "d1");
		addTestMapEquals(suite, toolResult, 4, args, "f1");
		addTestMapEquals(suite, toolResult, 5, args, "i1");
		addTestMapEquals(suite, toolResult, 6, args, "l1");
		addTestMapEquals(suite, toolResult, 7, args, "sh1");
		addTestMapEquals(suite, toolResult, 8, args, "as1");
		addTestMapEquals(suite, toolResult, 9, args, "ai1");
		
		
		suite.addTest(new TestCase("Call Complex Tool") {
			@Override
			protected void runTest() throws Throwable {
				
				Person person = new Person();
				person.setName("Jeremy");
				
				Person father = new Person();
				father.setName("Ken");
				person.setParents(new Person[] { father });
				
				Address address = new Address();
				address.setStreet(new String[] { "100 Main Street", "PO Box 123" });
				address.setCity("Miami");
				person.setAddress(address);
				
				Map<String, Object> argsComplex = new HashMap<String, Object>();
				argsComplex.put("PERSON", person);
				argsComplex.put("address", address);
				
				toolResult[0] = client.callTool(
					    new CallToolRequest("test-hello-world-complex",
					    		argsComplex));
				
				Content content = toolResult[0].content().get(0);
				String result = ((TextContent)content).text();
				
				JsonElement e = JsonParser.parseString(result);
				String name = e.getAsJsonObject().get("name").getAsString();
				JsonArray streetAddress = e.getAsJsonObject().get("address").getAsJsonObject().get("street").getAsJsonArray();

				Assert.assertEquals("Validate complex response: name", e.getAsJsonObject().get("name").getAsString(), "Jeremy");
				Assert.assertEquals("Validate complex response: street address 1", streetAddress.get(0).getAsString(), "My Greeting Is:");
				Assert.assertEquals("Validate complex response: street address 2", streetAddress.get(1).getAsString(), "Hello Jeremy from Miami son of Ken");

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
