package org.eclipse.mcp.test.junit.plugin;

import org.eclipse.mcp.factory.IFactory;
import org.eclipse.mcp.factory.IFactoryProvider;
import org.eclipse.mcp.internal.ExtensionManager;
import org.eclipse.mcp.test.junit.MCPFactoryTest;
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
		
		String contributorId = "junit.mcp.contributor";
	
			        
		ExtensionManager.Contributor contributor = manager.getContributor(contributorId);

		String name="JUnit Contributor";
		String description="JUnit Contributor Description";
		String provider="IBM";
		String capabilityId="IBM";

		// Test Factory
		addTestTrue(suite, "mcp factory", contributor != null);
		addTestEquals(suite, "mcp factory id", contributor.getId(), contributorId);
		addTestEquals(suite, "mcp factory.getName", contributor.getName(), name);
		addTestEquals(suite, "mcp factory.getDescription", contributor.getDescription(), description);
		addTestEquals(suite, "mcp factory.getProvider", contributor.getProvider(), provider);
		
		IFactory mcpFactory = contributor.getFactories()[0];		

		MCPFactoryTest.addFactoryTests(suite, ((IFactoryProvider)mcpFactory).createToolFactories()[0]);
		MCPFactoryTest.addResourceTemplateTests(suite, ((IFactoryProvider)mcpFactory).createResourceTemplateFactories()[0]);

		addTestEquals(suite, "suite size", suite.testCount() + 1, 53);

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
