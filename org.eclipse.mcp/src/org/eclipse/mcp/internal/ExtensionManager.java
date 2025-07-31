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
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mcp.IMCPResourceFactory;
import org.eclipse.mcp.IMCPTool;
import org.eclipse.mcp.Tracer;

/**
 * 
 */
public class ExtensionManager {
	
	Map<String, Server> servers = new HashMap<String, Server>();
	Map<String, Tool> tools = new HashMap<String, Tool>();
	Map<String, ResourceFactory> resourceFactories = new HashMap<String, ResourceFactory>();
	
	enum ELEMENT { tool, server, toolServerBinding, defaultEnablement }
	
	public ExtensionManager() {
		IExtensionRegistry extReg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensionElements = extReg.getConfigurationElementsFor("org.eclipse.mcp.modelContextProtocolServer");
		
		for (IConfigurationElement extensionElement: extensionElements) {
			if ("server".equals(extensionElement.getName())) {
				Server s = new Server(extensionElement);
				servers.put(s.id, s);
			} else if ("tool".equals(extensionElement.getName())) {
				Tool t = new Tool(extensionElement);
				tools.put(t.id, t);
			} else if ("resourceFactory".equals(extensionElement.getName())) {
				ResourceFactory rf = new ResourceFactory(extensionElement);
				resourceFactories.put(rf.id, rf);
			}
		}
		
		for (IConfigurationElement extensionElement: extensionElements) {
			if ("toolServerBinding".equals(extensionElement.getName())) {
				String serverId = extensionElement.getAttribute("serverId");
				String toolId = extensionElement.getAttribute("toolId");
				if (servers.containsKey(serverId)) {
					if (tools.containsKey(toolId)) {
						servers.get(serverId).addTool(tools.get(toolId));
					} else {
						Tracer.trace().trace(Tracer.EXTENSION, "toolServerBinding toolId not found: " + toolId);				
					}
				} else {
					Tracer.trace().trace(Tracer.EXTENSION, "toolServerBinding serverId not found: " + serverId);
				}
			} if ("resourceFactoryServerBinding".equals(extensionElement.getName())) {
				String serverId = extensionElement.getAttribute("serverId");
				String resourceFactoryId = extensionElement.getAttribute("resourceFactoryId");
				if (servers.containsKey(serverId)) {
					if (resourceFactories.containsKey(resourceFactoryId)) {
						servers.get(serverId).addResourceFactory(resourceFactories.get(resourceFactoryId));
					} else {
						Tracer.trace().trace(Tracer.EXTENSION, "toolServerBinding toolId not found: " + resourceFactoryId);				
					}
				} else {
					Tracer.trace().trace(Tracer.EXTENSION, "toolServerBinding serverId not found: " + serverId);
				}
			}
		}
	}
	
	public ExtensionManager.Server[] getServers() {
		return servers.values().toArray(new Server[0]);
	}
	
	public Tool getTool(String id) {
		return tools.get(id);
	}
	
	public ResourceFactory getResourceFactory(String id) {
		return resourceFactories.get(id);
	}
	
	public class Server {

		String id, name, version, description, defaultPort;
		boolean serveHttp = true;
		
		List<Tool> tools;
		List<ResourceFactory> resourceFactories;
		
		public Server(IConfigurationElement e) {
			this.id =  e.getAttribute("id"); 
			this.name = e.getAttribute("name");
			this.description = e.getAttribute("description");
			this.version = e.getAttribute("version");
			this.defaultPort = e.getAttribute("defaultPort");
			tools = new ArrayList<Tool>();
			resourceFactories = new ArrayList<ResourceFactory>();
		}
		
		public void addTool(Tool t) {
			tools.add(t);
		}
		
		public void addResourceFactory(ResourceFactory factory) {
			resourceFactories.add(factory);
		}
		
		public Tool[] getTools() {
			return tools.toArray(new Tool[0]);
		}
		
		public ResourceFactory[] getResourceFactories() {
			return resourceFactories.toArray(new ResourceFactory[0]);
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
	
	public class Tool {

		String id, name, description, schema;
		IMCPTool implementation;
		boolean isValid;
		
		public Tool(IConfigurationElement e) {
			this.id =  e.getAttribute("id"); 
			this.name = e.getAttribute("name");
			this.description = e.getAttribute("description");
			this.schema = e.getAttribute("schema");
			
			try {
				Object impl = e.createExecutableExtension("class");
				if (impl instanceof IMCPTool) {
					implementation = (IMCPTool)impl;
				} else {
					Tracer.trace().trace(Tracer.EXTENSION, impl.getClass() + " not instance of ITool; " + toString());
					isValid = false;
				}
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public String getSchema() {
			return schema;
		}

		public IMCPTool getImplementation() {
			return implementation;
		}

		public boolean isValid() {
			return isValid;
		}
	}
	
	
	public class ResourceFactory {

		String id, name, description;
		IMCPResourceFactory implementation;
		boolean isValid;
		
		public ResourceFactory(IConfigurationElement e) {
			this.id =  e.getAttribute("id"); 
			this.name = e.getAttribute("name");
			this.description = e.getAttribute("description");
			
			try {
				Object impl = e.createExecutableExtension("class");
				if (impl instanceof IMCPResourceFactory) {
					implementation = (IMCPResourceFactory)impl;
				} else {
					Tracer.trace().trace(Tracer.EXTENSION, impl.getClass() + " not instance of ITool; " + toString());
					isValid = false;
				}
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public IMCPResourceFactory getImplementation() {
			return implementation;
		}

		public boolean isValid() {
			return isValid;
		}
	}
}