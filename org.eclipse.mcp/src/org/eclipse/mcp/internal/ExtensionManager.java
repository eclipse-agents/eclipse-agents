/*******************************************************************************
 Licensed Materials - Property of IBM
 (C) Copyright IBM Corporation 2024. All Rights Reserved.
 *
 Note to U.S. Government Users Restricted Rights:
 Use, duplication or disclosure restricted by GSA ADP Schedule
 Contract with IBM Corp.
*******************************************************************************/
package org.eclipse.mcp.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mcp.IMCPFactory;
import org.eclipse.mcp.IMCPResourceTemplateFactory;
import org.eclipse.mcp.MCPToolFactory;

import io.modelcontextprotocol.server.McpServerFeatures.SyncCompletionSpecification;
import io.modelcontextprotocol.server.McpServerFeatures.SyncResourceSpecification;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.spec.McpSchema.ResourceTemplate;
import io.modelcontextprotocol.spec.McpSchema.Tool;


/**
 * 
 */
public class ExtensionManager {
	
	Map<String, Factory> factories = new HashMap<String, Factory>();
	
	enum ELEMENT { factory }
	
	public ExtensionManager() {
//		IExtensionRegistry extReg = Platform.getExtensionRegistry();
//		IConfigurationElement[] extensionElements = extReg.getConfigurationElementsFor("org.eclipse.mcp.modelContextProtocolServer");
		
		IExtensionRegistry extReg = Platform.getExtensionRegistry();
		IExtensionPoint point = extReg.getExtensionPoint("org.eclipse.mcp.modelContextProtocolServer"); //$NON-NLS-1$
		
		for (IExtension extension : point.getExtensions()) {
			for (IConfigurationElement extensionElement: extension.getConfigurationElements()) {
				if ("factory".equals(extensionElement.getName())) {
					Factory f = new Factory(extensionElement);
					if (f.errorMessage == null) {
						factories.put(f.id, f);
					} else {
						trace(extensionElement, f.errorMessage, null);
					}
				}
			}
		}
	}
	
	public ExtensionManager.Factory[] getFactories() {
		return factories.values().toArray(Factory[]::new);
	}
	
	public class Factory {

		String id, name, description, provider;
		IMCPFactory implementation;
		String errorMessage = null;
		Throwable toolThrowable = null;

		Map<ResourceTemplate, SyncCompletionSpecification> templateCompletions = new HashMap<ResourceTemplate, SyncCompletionSpecification>();
		Map<ResourceTemplate, SyncResourceSpecification> templateSpecifications = new HashMap<ResourceTemplate, SyncResourceSpecification>();
		Map<Tool, SyncToolSpecification> toolSpecifications = new HashMap<Tool, SyncToolSpecification>();
		
		public Factory(IConfigurationElement e) {
			this.id =  e.getAttribute("id"); 
			this.name = e.getAttribute("name");
			this.description = e.getAttribute("description");
			this.provider = e.getAttribute("provider");
			
			if (getId() == null || getId().isBlank()) {
				errorMessage = "Missing Tool id";
			} else if (getName() == null || getName().isBlank()) {
				errorMessage = "Missing Tool name";
			} else if (e.getAttribute("class") == null || e.getAttribute("class").isBlank()) {
				errorMessage = "Missing  Tool class";
			}
			
			if (errorMessage == null) {
				try {
					Object impl = e.createExecutableExtension("class");
					if (impl instanceof IMCPFactory) {
						implementation = (IMCPFactory)impl;
					} else {
						errorMessage = "Factory class " + e.getAttribute("class") + " not instanceof IMCPFactory";
					}
				} catch (CoreException ex) {
					errorMessage = "Factory class " + e.getAttribute("class") + "failed instantiation";
					toolThrowable = ex;
				}
			}
			
			if (errorMessage == null) {
				try {
					for (IMCPResourceTemplateFactory templateFactory: implementation.createResourceTemplateFactories()) {
						for (ResourceTemplate template: templateFactory.createResourceTemplates()) {
							templateCompletions.put(template, templateFactory.createCompletionSpecification(template));
							templateSpecifications.put(template, templateFactory.getResourceTemplateSpecification(template));
						}
					}
					
					for (MCPToolFactory toolFactory: implementation.createTools()) {
						Tool tool = toolFactory.createTool();
						SyncToolSpecification spec = toolFactory.createSpec(tool);
						toolSpecifications.put(tool, spec);
					}
				} catch (Exception ex) {
					errorMessage = "Factory class " + e.getAttribute("class") + "failed construction";
					toolThrowable = ex;
				}
			}
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}
		
		public String getProvider() {
			return provider;
		}

		public String getDescription() {
			return description;
		}

		public IMCPFactory getImplementation() {
			return implementation;
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