package org.eclipse.mcp.internal.preferences;

import org.eclipse.jface.dialogs.IDialogSettings;

public class UserServer implements IPreferencedServer {

	IDialogSettings settings;
	String id, name, version, description, httpPort;
	boolean serveHttp = false, enabled = false;

	String[] toolIds;
	String[] resourceFactoryIds;

	public UserServer(String id, String name, String version, String description, String httpPort, boolean serveHttp,
			boolean enabled, String[] toolIds, String[] resourceFactoryIds) {
		super();
		this.id = id;
		this.name = name;
		this.version = version;
		this.description = description;
		this.httpPort = httpPort;
		this.serveHttp = serveHttp;
		this.enabled = enabled;
		this.toolIds = toolIds;
		this.resourceFactoryIds = resourceFactoryIds;
	}

	public UserServer(IDialogSettings serverSettings) {
		this.settings = serverSettings;

		this.id = settings.get(ID);
		this.name = settings.get(NAME);
		this.version = settings.get(VERSION);
		this.description = settings.get(DESCRIPTION);

		this.serveHttp = settings.getBoolean(SERVEHTTP);
		this.enabled = settings.getBoolean(ENABLED);

		toolIds = settings.getArray(ADDED_TOOLS);
		resourceFactoryIds = settings.getArray(REMOVED_TOOLS);
	}

	public void save() {
		settings.put(ID, id);
		settings.put(NAME, name);
		settings.put(VERSION, version);
		settings.put(DESCRIPTION, description);

		settings.put(SERVEHTTP, serveHttp);
		settings.put(ENABLED, enabled);

		settings.put(ADDED_TOOLS, toolIds);
		settings.put(ADDED_RESOURCES, resourceFactoryIds);
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getHttpPort() {
		return httpPort;
	}

	public void setHttpPort(String httpPort) {
		this.httpPort = httpPort;
	}

	@Override
	public boolean isServeHttp() {
		return serveHttp;
	}

	public void setServeHttp(boolean serveHttp) {
		this.serveHttp = serveHttp;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String[] getToolIds() {
		return toolIds;
	}
	
	public void setToolIds(String[] toolIds) {
		this.toolIds = toolIds;
	}

	public void setResourceFactorieIds(String[] resourceFactorieIds) {
		this.resourceFactoryIds = resourceFactorieIds;
	}

	@Override
	public String[] getResourceFactoryIds() {
		return resourceFactoryIds;
	}

	@Override
	public String[] setToolIds() {
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
}
