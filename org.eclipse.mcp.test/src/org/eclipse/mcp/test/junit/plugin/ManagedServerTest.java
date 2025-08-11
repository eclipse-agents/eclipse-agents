package org.eclipse.mcp.test.junit.plugin;

import org.eclipse.mcp.internal.ExtensionManager;
import org.eclipse.mcp.internal.ManagedServer;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

@RunWith(AllTests.class)
public final class ManagedServerTest {

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
		ManagedServer server = new ManagedServer( manager.getServer(serverId));
		
		// Test Tool
		suite.addTest(new TestCase("Start Server") {
			@Override
			protected void runTest() throws Throwable {
				server.start();
			}
		});
		
		suite.addTest(new TestCase("Start Server") {
			@Override
			protected void runTest() throws Throwable {
				
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
}
