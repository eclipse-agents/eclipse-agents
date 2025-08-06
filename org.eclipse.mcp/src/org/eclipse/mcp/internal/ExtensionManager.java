/*******************************************************************************
 Licensed Materials - Property of IBM
 (C) Copyright IBM Corporation 2024. All Rights Reserved.
 *
 Note to U.S. Government Users Restricted Rights:
 Use, duplication or disclosure restricted by GSA ADP Schedule
 Contract with IBM Corp.
*******************************************************************************/
package org.eclipse.mcp.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mcp.IMCPResourceController;
import org.eclipse.mcp.IMCPTool;
import org.eclipse.mcp.internal.preferences.ServerElement;

import com.google.gson.JsonParser;


/**
 * 
 */
public class ExtensionManager {
	
	Map<String, Server> servers = new HashMap<String, Server>();
	Map<String, String> categories = new HashMap<String, String>();
	Map<String, Tool> tools = new HashMap<String, Tool>();
	Map<String, ResourceController> resourceControllers = new HashMap<String, ResourceController>();
	
	enum ELEMENT { tool, server, category, toolServerBinding, defaultEnablement }
	
	public ExtensionManager() {
//		IExtensionRegistry extReg = Platform.getExtensionRegistry();
//		IConfigurationElement[] extensionElements = extReg.getConfigurationElementsFor("org.eclipse.mcp.modelContextProtocolServer");
		
		IExtensionRegistry extReg = Platform.getExtensionRegistry();
		IExtensionPoint point = extReg.getExtensionPoint("org.eclipse.mcp.modelContextProtocolServer"); //$NON-NLS-1$
		
		for (IExtension extension : point.getExtensions()) {
			for (IConfigurationElement extensionElement: extension.getConfigurationElements()) {
				if ("server".equals(extensionElement.getName())) {
					Server s = new Server(extensionElement);
					if (s.errorMessage == null) {
						servers.put(s.id, s);
					} else {
						trace(extensionElement, s.errorMessage, null);
					}
				} else if ("tool".equals(extensionElement.getName())) {
					Tool t = new Tool(extensionElement);
					if (t.errorMessage == null) {
						tools.put(t.id, t);
						for (IConfigurationElement child: extensionElement.getChildren()) {
							if (child.getName().equals("propertyPage")) {
								t.addPropertyEditorId(child.getAttribute("id"));
								//TODO validate id;
							}
						}
					} else {
						trace(extensionElement, t.errorMessage, t.toolThrowable);
					}
				} else if ("resourceController".equals(extensionElement.getName())) {
					ResourceController rf = new ResourceController(extensionElement);
					if (rf.errorMessage == null) {
						resourceControllers.put(rf.id, rf);
						for (IConfigurationElement child: extensionElement.getChildren()) {
							if (child.getName().equals("propertyPage")) {
								rf.addPropertyEditorId(child.getAttribute("id"));
								//TODO validate id;
							}
						}
					} else {
						trace(extensionElement, rf.errorMessage, rf.resourceControllerThrowable);
					}
				} else if ("category".equals(extensionElement.getName())) {
					String id = extensionElement.getAttribute("id");
					String name = extensionElement.getAttribute("name");
					
					if (id == null || id.isBlank()) {
						trace(extensionElement, "Missing category id", null);
					} else if (name == null || name.isBlank()) {
						trace(extensionElement, "Missing category name", null);
					} else {
						categories.put(id, name);
					}
				}
			}
		}
			
		for (IExtension extension : point.getExtensions()) {
			for (IConfigurationElement extensionElement : extension.getConfigurationElements()) {
				if ("toolServerBinding".equals(extensionElement.getName())) {
					String serverId = extensionElement.getAttribute("serverId");
					String toolId = extensionElement.getAttribute("toolId");
					if (servers.containsKey(serverId)) {
						if (tools.containsKey(toolId)) {
							servers.get(serverId).addTool(tools.get(toolId));
						} else {
							trace(extensionElement, "toolServerBinding toolId not found: " + toolId, null);			
						}
					} else {
						trace(extensionElement, "toolServerBinding serverId not found: " + serverId, null);
					}
				} if ("resourceControllerServerBinding".equals(extensionElement.getName())) {
					String serverId = extensionElement.getAttribute("serverId");
					String resourceControllerId = extensionElement.getAttribute("resourceControllerId");
					if (servers.containsKey(serverId)) {
						if (resourceControllers.containsKey(resourceControllerId)) {
							servers.get(serverId).addResourceFactory(resourceControllers.get(resourceControllerId));
						} else {
							trace(extensionElement, "resourceControllerServerBinding resourceControllerId not found: " + resourceControllerId, null);				
						}
					} else {
						trace(extensionElement, "resourceControllerServerBinding serverId not found: " + serverId, null);
					}
				}
			}
		}
	}
	
	public ExtensionManager.Server[] getServers() {
		return servers.values().toArray(new Server[0]);
	}
	
	public ExtensionManager.Server getServer(String id) {
		return servers.get(id);
	}
	
	public Tool getTool(String id) {
		return tools.get(id);
	}
	
	public ResourceController getResourceController(String id) {
		return resourceControllers.get(id);
	}
	
	public class Server {

		String id, name, version, description, defaultPort;
		boolean serveHttp = true;
		
		List<Tool> tools;
		List<ResourceController> resourceController;
		String errorMessage = null;
		//TODO
		String categoryId;
		
		public Server(IConfigurationElement e) {
			this.id =  e.getAttribute("id"); 
			this.name = e.getAttribute("name");
			this.description = e.getAttribute("description");
			this.version = e.getAttribute("version");
			this.defaultPort = e.getAttribute("defaultPort");
			tools = new ArrayList<Tool>();
			resourceController = new ArrayList<ResourceController>();
			
			if (getId() == null || getId().isBlank()) {
				errorMessage = "Missing Server id";
			} else if (getName() == null || getName().isBlank()) {
				errorMessage = "Missing Server name";
			} else if (getVersion() == null || getVersion().isBlank()) {
				errorMessage = "Missing Server Version";
			} else if (getDefaultPort() == null || getDefaultPort().isBlank()) {
				errorMessage = "Missing Server Default Port";
			} else {
				try {
					Integer.parseInt(getDefaultPort());
				} catch (NumberFormatException ex) {
					errorMessage = "Invalid Server Default Port: " + getDefaultPort();
				}
			}
		}
		
		public void addTool(Tool t) {
			tools.add(t);
		}
		
		public void addResourceFactory(ResourceController factory) {
			resourceController.add(factory);
		}
		
		public Tool[] getTools() {
			return tools.toArray(new Tool[0]);
		}
		
		public ResourceController[] getResourceControllers() {
			return resourceController.toArray(new ResourceController[0]);
		}
		
		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getVersion() {
			return version;
		}

		public String getDescription() {
			return description;
		}

		public String getDefaultPort() {
			return defaultPort;
		}
		
		public boolean getServerHttp() {
			return serveHttp;
		}
	}
	
	public class Tool implements ServerElement {

		String id, name, description, schema, categoryId;
		IMCPTool implementation;
		List<String> propertyPageIds = new ArrayList<String>();
		String errorMessage = null;
		Throwable toolThrowable = null;
		
		public Tool(IConfigurationElement e) {
			this.id =  e.getAttribute("id"); 
			this.name = e.getAttribute("name");
			this.description = e.getAttribute("description");
			this.schema = e.getAttribute("schema");
			this.categoryId = e.getAttribute("categoryId");
			
			if (getId() == null || getId().isBlank()) {
				errorMessage = "Missing Tool id";
			} else if (getName() == null || getName().isBlank()) {
				errorMessage = "Missing Tool name";
			} else if (e.getAttribute("class") == null || e.getAttribute("class").isBlank()) {
				errorMessage = "Missing  Tool class";
			} else if (getSchema() == null || getSchema().isBlank()) {
				errorMessage = "Missing Tool schema";
			}
			
			if (errorMessage == null) {
				try {
					JsonParser.parseString(schema).getAsJsonObject();
				} catch (Exception ex) {
					errorMessage = "Tool Schema Parse Failure: \"" + schema + "\"";
					toolThrowable = ex;
				}
			}
			
			if (errorMessage == null) {
				try {
					Object impl = e.createExecutableExtension("class");
					if (impl instanceof IMCPTool) {
						implementation = (IMCPTool)impl;
					} else {
						errorMessage = "Tool class " + e.getAttribute("class") + " not instanceof IMCPTool";
					}
				} catch (CoreException ex) {
					errorMessage = "Tool class " + e.getAttribute("class") + "failed instantiation";
					toolThrowable = ex;
				}
			}
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public String getCategory() {
			if (categories.containsKey(categoryId)) {
				return categories.get(categoryId);
			}
			return "";
		}

		@Override
		public String getDescription() {
			return description;
		}

		public String getSchema() {
			return schema;
		}

		public IMCPTool getImplementation() {
			return implementation;
		}

		@Override
		public <T> T getAdapter(Class<T> arg0) {
			return null;
		}

		@Override
		public String[] getPropertyEditorIds() {
			return propertyPageIds.toArray(String[]::new);
		}

		@Override
		public void addPropertyEditorId(String id) {
			propertyPageIds.add(id);
		}
	}
	
	
	public class ResourceController implements ServerElement {

		String id, name, description, categoryId;
		List<String> propertyPageIds = new ArrayList<String>();
		IMCPResourceController implementation;

		String errorMessage = null;
		Throwable resourceControllerThrowable = null;
		
		public ResourceController(IConfigurationElement e) {
			this.id =  e.getAttribute("id"); 
			this.name = e.getAttribute("name");
			this.description = e.getAttribute("description");
			this.categoryId = e.getAttribute("categoryId");
			
			if (getId() == null || getId().isBlank()) {
				errorMessage = "Missing ResourceController id";
			} else if (getName() == null || getName().isBlank()) {
				errorMessage = "Missing ResourceController name";
			} else if (e.getAttribute("class") == null || e.getAttribute("class").isBlank()) {
				errorMessage = "Missing ResourceController class";
			}
			
			if (errorMessage == null) {
				try {
					Object impl = e.createExecutableExtension("class");
					if (impl instanceof IMCPResourceController) {
						implementation = (IMCPResourceController)impl;
					} else {
						errorMessage = "ResourceController class " + e.getAttribute("class") + " not instanceof IMCPResourceController";
					}
				} catch (CoreException ex) {
					errorMessage = "ResourceController class " + e.getAttribute("class") + " failed instantiation";;
					resourceControllerThrowable = ex;
				}
			}
			
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public String getCategory() {
			if (categories.containsKey(categoryId)) {
				return categories.get(categoryId);
			}
			return "";
		}

		@Override
		public String getDescription() {
			return description;
		}

		public IMCPResourceController getImplementation() {
			return implementation;
		}

		@Override
		public <T> T getAdapter(Class<T> arg0) {
			return null;
		}

		@Override
		public String[] getPropertyEditorIds() {
			return propertyPageIds.toArray(String[]::new);
		}

		@Override
		public void addPropertyEditorId(String id) {
			propertyPageIds.add(id);
		}
	}
	
	private void trace(IConfigurationElement extensionElement, String message, Throwable t) {
		String identifier = extensionElement.getAttribute("id");
		if (identifier == null) {
			identifier = extensionElement.getNamespaceIdentifier();
		}
//		String namespace = extensionElement.getNamespaceIdentifier();
//		namespace = (namespace == null) ? "Undefined" : namespace;
		
		String output = "[" + identifier +"]:: " + message;
		
		
//		System.err.println(output);

		if (t != null) {
			Tracer.trace().trace(Tracer.IMPLEMENTATIONS, output, t);
		} else {
			Tracer.trace().trace(Tracer.IMPLEMENTATIONS, output);
		}
	}
}