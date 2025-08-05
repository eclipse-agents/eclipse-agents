package org.eclipse.mcp.internal.preferences;

import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.mcp.internal.ExtensionManager;
import org.eclipse.mcp.internal.ExtensionManager.ResourceController;
import org.eclipse.mcp.internal.ExtensionManager.Tool;

public class ExtensionServer implements IPreferencedServer {

	DialogSettings settings  = null;
	DialogSettings parentSettings = null;

	ExtensionManager.Server extensionServer = null;

	String customName = null;
	String customDescription = null;
	String customVersion = null;
	String customHttpPort = null;
	Boolean customServeHttp = null;
	Boolean customEnabled = null;

	String[] defaultToolIds;
	String[] defaultResourceFactoryIds;
	
	String[] enabledToolIds = null;
	String[] enabledResourceFactoryIds = null;

	String[] removedToolIds = null;
	String[] removedResourceFactoryIds = null;
	
	String[] addedToolIds = null;
	String[] addedResourceFactoryIds = null;
	

	public ExtensionServer(ExtensionManager.Server extensionServer, DialogSettings serverSettings, DialogSettings parentSettings) {
		
		this.extensionServer = extensionServer;
		this.settings = serverSettings;
		this.parentSettings = parentSettings;
		
		if (serverSettings != null) {
			customName = serverSettings.get(NAME);
			customDescription = serverSettings.get(DESCRIPTION);
			customVersion = serverSettings.get(VERSION);
			customHttpPort = serverSettings.get(HTTPPORT);
	
			if (serverSettings.get(SERVEHTTP) != null) {
				customServeHttp = serverSettings.getBoolean(SERVEHTTP);
			} else {
				customServeHttp = null;
			}
			
			if (serverSettings.get(ENABLED) != null) {
				customEnabled = serverSettings.getBoolean(ENABLED);
			} else {
				customServeHttp = null;
			}
			
			enabledToolIds = serverSettings.getArray(ENABLED_TOOLS);
			enabledResourceFactoryIds = serverSettings.getArray(ENABLED_RESOURCES);
	
			removedToolIds = serverSettings.getArray(REMOVED_TOOLS);
			removedResourceFactoryIds = serverSettings.getArray(REMOVED_RESOURCES);
			
			addedToolIds = serverSettings.getArray(ADDED_TOOLS);
			addedResourceFactoryIds = serverSettings.getArray(ADDED_RESOURCES);
		}
	}
	
	public void save() {

		settings.put(NAME, customName);
		settings.put(DESCRIPTION, customDescription);
		settings.put(VERSION, customVersion);
		settings.put(HTTPPORT, customHttpPort);

		if (customServeHttp != null) {
			settings.put(SERVEHTTP, customServeHttp ? "true" : "false");
		} else {
			settings.put(SERVEHTTP, (String)null);
		}
		
		if (customEnabled != null) {
			settings.put(ENABLED, customEnabled ? "true" : "false");
		} else {
			settings.put(ENABLED, (String)null);
		}

	}
	
	public void delete() {
		parentSettings.removeSection(settings);
	}

	@Override
	public String getId() {
		return extensionServer.getId();
	}

	@Override
	public String getName() {
		return customName == null ? extensionServer.getName() : customName;
	}

	@Override
	public void setName(String name) {
		customName = name;
	}

	@Override
	public String getVersion() {
		return customVersion == null ? extensionServer.getVersion() : customVersion;
	}

	@Override
	public void setVersion(String version) {
		this.customVersion = version;
	}

	@Override
	public String getDescription() {
		return customDescription== null ? extensionServer.getDescription() : customDescription;
	}

	@Override
	public void setDescription(String description) {
		this.customDescription = description;
	}

	@Override
	public String getHttpPort() {
		return customHttpPort == null ? extensionServer.getDefaultPort() : customHttpPort;
	}

	@Override
	public void setHttpPort(String httpPort) {
		this.customHttpPort = httpPort;
		
	}

	@Override
	public boolean isServeHttp() {
		return customServeHttp == null ? extensionServer.getServerHttp() : customServeHttp;
	}

	@Override
	public void setServeHttp(boolean serveHttp) {
		this.customServeHttp = serveHttp;
	}

	@Override
	public boolean isEnabled() {
		return customEnabled == null ? true : customEnabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.customEnabled = enabled;
	}

	@Override
	public String[] getToolIds() {
		return null;
	}

	@Override
	public String[] setToolIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getResourceFactoryIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] setResourceFactoryIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getEnabledToolIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEnabledToolIds(String[] toolIds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getEnabledResourceFactoryIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEnabledResourceFactoryIds(String[] resourceFactoryIds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Tool[] getTools() {
		return extensionServer.getTools();
	}
	
	@Override
	public ResourceController[] getResourceFactories() {
		return extensionServer.getResourceControllers();
	}

	@Override
	public DialogSettings getElementSettings(String elementId, String propertiesId) {
		DialogSettings elementsSection = (DialogSettings)settings.getSection(TOOL_PREFERENCES);
		if (elementsSection != null) {
			DialogSettings elementSection  = (DialogSettings)elementsSection.getSection(elementId);
			if (elementSection != null) {
				DialogSettings propertyEditorSection  = (DialogSettings)elementSection.getSection(propertiesId);
				if (propertyEditorSection != null) {
					return propertyEditorSection;
				}
			}
		}
		
		return new DialogSettings(propertiesId);
	}

	@Override
	public void setElementSettings(String elementId, String propertiesId, DialogSettings toolSettings) {
		DialogSettings elementsSection = (DialogSettings)DialogSettings.getOrCreateSection(settings, TOOL_PREFERENCES);;
		DialogSettings elementSection  = (DialogSettings)DialogSettings.getOrCreateSection(elementsSection, elementId);;
		
		
		if (elementSection.getSection(toolSettings.getName()) != null) {
			elementSection.removeSection(toolSettings.getName());
		}

		elementSection.addSection(toolSettings);
	}
	
}
