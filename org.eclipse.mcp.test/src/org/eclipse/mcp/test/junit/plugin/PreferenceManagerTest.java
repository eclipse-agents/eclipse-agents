package org.eclipse.mcp.test.junit.plugin;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.mcp.internal.ElementProperties;
import org.eclipse.mcp.internal.Images;
import org.eclipse.mcp.internal.MCPElementPropertyInput;
import org.eclipse.mcp.internal.PreferenceManager;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

@RunWith(AllTests.class)
public final class PreferenceManagerTest {

	
	final static String serverId = "org.eclipse.mcp.test.junit.plugin.extension.server";
    final static String toolId = "org.eclipse.mcp.test.junit.plugin.extension.tool";
    final static String resourceControllerId = "org.eclipse.mcp.test.junit.plugin.extension.controller";
    final static String propertyPageId = "org.eclipse.mcp.test.junit.plugin.extension.SamplePropertyPage";

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();

		PreferenceManager manager = new PreferenceManager();
		
		manager.getServer(serverId);
		
		suite.addTest(new TestCase("Delete settings.txt") {
			@Override
			protected void runTest() throws Throwable {
				IPath path = org.eclipse.mcp.internal.Activator.getDefault().getStateLocation();
				String filename = path.append(PreferenceManager.fileName).toOSString();
				java.io.File file = new java.io.File(filename);
				if (file.exists()) {
					file.delete();
				} else {
					System.out.println("settings.txt not found");
				}
				Assert.isTrue(!file.exists(), "settings.txt does not exist");
			}
		});
		
		suite.addTest(new TestCase("Re-create settings.txt") {
			@Override
			protected void runTest() throws Throwable {
				PreferenceManager manager = new PreferenceManager();
				IPath path = org.eclipse.mcp.internal.Activator.getDefault().getStateLocation();
				String filename = path.append(PreferenceManager.fileName).toOSString();
				java.io.File file = new java.io.File(filename);
				Assert.isTrue(file.exists(), "settings.txt exists");
			}
		});
		
		suite.addTest(new TestCase("Read default, empty server values") {
			@Override
			protected void runTest() throws Throwable {
				PreferenceManager manager = new PreferenceManager();
				manager.load();
			}
		});
		
		suite.addTest(new TestCase("Test Empty ElementProperties") {
			@Override
			protected void runTest() throws Throwable {
				PreferenceManager manager = new PreferenceManager();
				manager.load();
				ElementProperties properties = new ElementProperties(serverId, toolId, "tool.name", Images.IMG_TOOL, new String[] { propertyPageId });
				IDialogSettings settings = properties.getProperties(propertyPageId);
				
				Assert.isTrue(settings.getName().equals(propertyPageId), "dialog settings name");
				Assert.isTrue(settings.getSections().length == 0, "dialog settings name");
			}
		});
		
		suite.addTest(new TestCase("Test Empty MCPElementPropertyInput") {
			@Override
			protected void runTest() throws Throwable {
				PreferenceManager manager = new PreferenceManager();
				manager.load();
				
				MCPElementPropertyInput input = new MCPElementPropertyInput(serverId, toolId, "tool.name", Images.IMG_TOOL, manager) ;
				Assert.isTrue(serverId.equals(input.getServerId()), "dialog settings name");
				Assert.isTrue(toolId.equals(input.getElementId()), "dialog settings name");
				
				DialogSettings settings = input.loadCurrentSettings(propertyPageId);
				
				Assert.isTrue(settings.getName().equals(propertyPageId), "dialog settings name");
				Assert.isTrue(settings.getSections().length == 0, "dialog settings name");
			}
		});
		
		suite.addTest(new TestCase("Save MCPElementPropertyInput") {
			@Override
			protected void runTest() throws Throwable {
				PreferenceManager manager = new PreferenceManager();
				manager.load();
				
				MCPElementPropertyInput input = new MCPElementPropertyInput(serverId, toolId, "tool.name", Images.IMG_TOOL, manager) ;
				DialogSettings settings = input.loadCurrentSettings(propertyPageId);
				
				settings.put("AAA", "BBB");
				settings.put("CCC", "DDD");
				IDialogSettings section1 = settings.addNewSection("section1");
				section1.put("EEE", "FFF");
				IDialogSettings section2 = settings.addNewSection("section2");
				section2.put("HHH", "III");
				
				input.applySettings(propertyPageId, settings);
			}
		});
		
		suite.addTest(new TestCase("Validate saved MCPElementPropertyInput") {
			@Override
			protected void runTest() throws Throwable {
				PreferenceManager manager = new PreferenceManager();
				manager.load();
				
				MCPElementPropertyInput input = new MCPElementPropertyInput(serverId, toolId, "tool.name", Images.IMG_TOOL, manager) ;
				DialogSettings settings = input.loadCurrentSettings(propertyPageId);
				
				Assert.isTrue("BBB".equals(settings.get("AAA")), "settings AAA");
				Assert.isTrue("DDD".equals(settings.get("CCC")), "settings AAA");
				
				IDialogSettings section1 = settings.getSection("section1");
				Assert.isTrue("FFF".equals(section1.get("EEE")), "section1 EEE");
				
				
				IDialogSettings section2 = settings.getSection("section2");
				Assert.isTrue("III".equals(section2.get("HHH")), "section2 HHH");

			}
		});
		
		suite.addTest(new TestCase("Update MCPElementPropertyInput") {
			@Override
			protected void runTest() throws Throwable {
				PreferenceManager manager = new PreferenceManager();
				manager.load();
				
				MCPElementPropertyInput input = new MCPElementPropertyInput(serverId, toolId, "tool.name", Images.IMG_TOOL, manager) ;
				DialogSettings settings = input.loadCurrentSettings(propertyPageId);
				
				settings.put("AAA", "XXX");
				settings.put("CCC", "YYY");
				IDialogSettings section1 = settings.getSection("section1");
				section1.put("EEE", "ZZZ");
				settings.removeSection("section2");
				IDialogSettings section3 = settings.addNewSection("section3");
				section3.put("111", "!!!");
				
				input.applySettings(propertyPageId, settings);
			}
		});
		
		suite.addTest(new TestCase("Validate saved MCPElementPropertyInput") {
			@Override
			protected void runTest() throws Throwable {
				PreferenceManager manager = new PreferenceManager();
				manager.load();
				
				MCPElementPropertyInput input = new MCPElementPropertyInput(serverId, toolId, "tool.name", Images.IMG_TOOL, manager) ;
				DialogSettings settings = input.loadCurrentSettings(propertyPageId);
				
				Assert.isTrue("XXX".equals(settings.get("AAA")), "settings AAA");
				Assert.isTrue("YYY".equals(settings.get("CCC")), "settings AAA");
				
				IDialogSettings section1 = settings.getSection("section1");
				Assert.isTrue("ZZZ".equals(section1.get("EEE")), "section1 EEE");
				
				Assert.isTrue(settings.getSection("section2") == null, "section 2 removed");
				
				
				IDialogSettings section3 = settings.getSection("section3");
				Assert.isTrue("!!!".equals(section3.get("111")), "section3 111");

			}
		});
		
		suite.addTest(new TestCase("Add a second Property Page") {
			@Override
			protected void runTest() throws Throwable {
				PreferenceManager manager = new PreferenceManager();
				manager.load();
				
				MCPElementPropertyInput input = new MCPElementPropertyInput(serverId, toolId , "tool.name", Images.IMG_TOOL, manager) ;
				DialogSettings settings = input.loadCurrentSettings(propertyPageId + ".second");
				
				settings.put("FOO", "BAR");
				settings.put("FIE", "BIE");
				
				input.applySettings(propertyPageId + ".second", settings);
			}
		});
		
		suite.addTest(new TestCase("Validate both property pages") {
			@Override
			protected void runTest() throws Throwable {
				PreferenceManager manager = new PreferenceManager();
				manager.load();
				
				MCPElementPropertyInput input = new MCPElementPropertyInput(serverId, toolId, "tool.name", Images.IMG_TOOL, manager) ;
				DialogSettings settings = input.loadCurrentSettings(propertyPageId);
				
				Assert.isTrue("XXX".equals(settings.get("AAA")), "settings AAA");
				Assert.isTrue("YYY".equals(settings.get("CCC")), "settings AAA");
				
				IDialogSettings section1 = settings.getSection("section1");
				Assert.isTrue("ZZZ".equals(section1.get("EEE")), "section1 EEE");
				
				Assert.isTrue(settings.getSection("section2") == null, "section 2 removed");
				
				
				IDialogSettings section3 = settings.getSection("section3");
				Assert.isTrue("!!!".equals(section3.get("111")), "section3 111");
				
				DialogSettings settings2 = input.loadCurrentSettings(propertyPageId + ".second");
				
				Assert.isTrue("BAR".equals(settings2.get("FOO")), "settings FOO");
				Assert.isTrue("BIE".equals(settings2.get("FIE")), "settings FIE");
				

			}
		});
		
		return suite;
	}
}
