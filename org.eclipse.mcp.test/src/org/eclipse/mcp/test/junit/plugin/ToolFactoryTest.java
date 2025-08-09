package org.eclipse.mcp.test.junit.plugin;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mcp.IMCPToolFactory;
import org.eclipse.mcp.test.junit.plugin.extension.MCPToolFactory;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

@RunWith(AllTests.class)
public final class ToolFactoryTest {

	/**
	 * 
	 */
	public static TestSuite suite() {
		TestSuite suite = new TestSuite();

		MCPToolFactory factory = new MCPToolFactory();
		
		IMCPToolFactory.IMCPAnnotatedTool[] tools = factory.createTools();
		
		// Test Server
//		addTestEquals(suite, "mcp server.getDefaultPort", server.getDefaultPort(), "server.port");

		for (IMCPToolFactory.IMCPAnnotatedTool tool: tools ) {
			System.out.println(tool.getId());
			System.out.println(tool.getInputSchema());
			
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
			String[] result = tool.apply(args, null);
			for (String s: result) {
				System.out.println(s);
			}
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
