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
import org.eclipse.mcp.IModelContextProtocolTool;
import org.eclipse.mcp.Tracer;

/**
 * To prevent having commands bound to editors contexts not present in the installation,
 * we manage our own contexts and synchronize their activation with the potentiality
 * of specific editor contexts being activated or deactivated.
 * 
 * When a command is bound to an editor id that doesn't exist, it still appears in the 
 * Keys preferences, bound to an editor id string, rather than an editor name.
 */
public class ExtensionManager {
	
	Map<String, Server> servers = new HashMap<String, Server>();
	Map<String, Tool> tools = new HashMap<String, Tool>();
	
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
			} else if ("defaultEnablement".equals(extensionElement.getName())) {
				//TODO
			}
		}
	}
	
	public ExtensionManager.Server[] getServers() {
		return servers.values().toArray(new Server[0]);
	}
	
	public class Server {

		String id, name, version, description, defaultPort;
		boolean serveHttp = true;
		
		List<Tool> tools;
		
		public Server(IConfigurationElement e) {
			this.id =  e.getAttribute("id"); 
			this.name = e.getAttribute("name");
			this.description = e.getAttribute("description");
			this.version = e.getAttribute("version");
			this.defaultPort = e.getAttribute("defaultPort");
			tools = new ArrayList<Tool>();
		}
		
		public void addTool(Tool t) {
			tools.add(t);
		}
		
		public Tool[] getTools() {
			return tools.toArray(new Tool[0]);
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
		IModelContextProtocolTool implementation;
		boolean isValid;
		
		public Tool(IConfigurationElement e) {
			this.id =  e.getAttribute("id"); 
			this.name = e.getAttribute("name");
			this.description = e.getAttribute("description");
			this.schema = e.getAttribute("schema");
			
			try {
				Object impl = e.createExecutableExtension("class");
				if (impl instanceof IModelContextProtocolTool) {
					implementation = (IModelContextProtocolTool)impl;
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

		public IModelContextProtocolTool getImplementation() {
			return implementation;
		}

		public boolean isValid() {
			return isValid;
		}
		
		
	}
}