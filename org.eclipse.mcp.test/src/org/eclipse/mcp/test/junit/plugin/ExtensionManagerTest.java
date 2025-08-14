package org.eclipse.mcp.test.junit.plugin;

import org.eclipse.mcp.IMCPFactory;
import org.eclipse.mcp.internal.ExtensionManager;
import org.eclipse.mcp.test.junit.MCPFactoryTest;
import org.eclipse.mcp.test.junit.plugin.extension.MCPFactory;
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
		
		String factoryId = "junit.mcp.factory";
		String name = "JUnit Factory";
		String description = "JUnit Factory Description";
		String provider = "IBM";
			        
		ExtensionManager.Factory factory = manager.getFactories(factoryId);

		// Test Factory
		addTestTrue(suite, "mcp factory", factory != null);
		addTestEquals(suite, "mcp factory id", factory.getId(), factoryId);
		addTestEquals(suite, "mcp factory.getName", factory.getName(), name);
		addTestEquals(suite, "mcp factory.getDescription", factory.getDescription(), description);
		addTestEquals(suite, "mcp factory.getProvider", factory.getProvider(), provider);
		
		IMCPFactory mcpFactory = factory.getImplementation();		

		MCPFactoryTest.addFactoryTests(suite,  (MCPFactory)mcpFactory);

		addTestEquals(suite, "suite size", suite.testCount() + 1, 48);

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
