package org.eclipse.mcp.internal.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mcp.Activator;
import org.eclipse.mcp.Tracer;
import org.eclipse.mcp.internal.ExtensionManager;

public interface IPreferencedServer {
	
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String VERSION = "version";
	public static final String DESCRIPTION = "description";
	public static final String SERVEHTTP = "serveHttp";
	public static final String HTTPPORT = "httpPort";
	public static final String ENABLED = "enabled";
	
	public static final String ADDED_TOOLS = "addedToolIds";
	public static final String ADDED_RESOURCES = "addedResourceFactoryIds";

	public static final String REMOVED_TOOLS = "removedToolIds";
	public static final String REMOVED_RESOURCES = "removedResourceFactoryIds";

	public static final String ENABLED_TOOLS = "enabledToolIds";
	public static final String ENABLED_RESOURCES = "enabledResourceFactoryIds";
	
	public String getId();
	public String getName();
	public void setName(String name);
	public String getVersion();
	public void setVersion(String version);
	public String getDescription();
	public void setDescription(String description);
	public String getHttpPort();
	public void setHttpPort(String httpPort);
	public boolean isServeHttp();
	public void setServeHttp(boolean serveHttp);
	public boolean isEnabled();
	public void setEnabled(boolean enabled);

	public String[] getToolIds();
	public String[] setToolIds();
	public String[] getResourceFactoryIds();
	public String[] setResourceFactoryIds();
	
	public String[] getEnabledToolIds();
	public void setEnabledToolIds(String[] toolIds);
	public String[] getEnabledResourceFactoryIds();
	public void setEnabledResourceFactoryIds(String[] resourceFactoryIds);
	
	default public ExtensionManager.Tool[] getTools() {
		List<ExtensionManager.Tool> tools = new ArrayList<ExtensionManager.Tool>();
		for (String id: getToolIds()) {
			ExtensionManager.Tool tool = Activator.getDefault().getExtensionManager().getTool(id);
			if (tool != null) {
				tools.add(tool);
			} else {
				Tracer.trace().trace(Tracer.EXTENSION, "tool id not found:" + id);
			}
		}
		return (ExtensionManager.Tool[])tools.toArray();
	}
	
	public default ExtensionManager.ResourceFactory[] getResourceFactories() {
		List<ExtensionManager.ResourceFactory> resourceFactories = new ArrayList<ExtensionManager.ResourceFactory>();
		for (String id: getResourceFactoryIds()) {
			ExtensionManager.ResourceFactory factory = Activator.getDefault().getExtensionManager().getResourceFactory(id);
			if (factory != null) {
				resourceFactories.add(factory);
			} else {
				Tracer.trace().trace(Tracer.EXTENSION, "resource factory id not found:" + id);
			}
		}
		return (ExtensionManager.ResourceFactory[])resourceFactories.toArray();
	}
}
