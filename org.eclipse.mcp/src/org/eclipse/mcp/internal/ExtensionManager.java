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
import org.eclipse.mcp.factory.IFactory;
import org.eclipse.mcp.factory.IResourceFactory;
import org.eclipse.mcp.factory.IResourceTemplateFactory;
import org.eclipse.mcp.factory.IToolFactory;

import io.modelcontextprotocol.server.McpServerFeatures.SyncCompletionSpecification;
import io.modelcontextprotocol.server.McpServerFeatures.SyncResourceSpecification;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.spec.McpSchema.ResourceTemplate;
import io.modelcontextprotocol.spec.McpSchema.Tool;


/**
 * 
 */
public class ExtensionManager {
	
	Map<String, Contributor> contributors = new HashMap<String, Contributor>();
	
	enum ELEMENT { factory }
	
	public ExtensionManager() {
//		IExtensionRegistry extReg = Platform.getExtensionRegistry();
//		IConfigurationElement[] extensionElements = extReg.getConfigurationElementsFor("org.eclipse.mcp.modelContextProtocolServer");
		
		IExtensionRegistry extReg = Platform.getExtensionRegistry();
		IExtensionPoint point = extReg.getExtensionPoint("org.eclipse.mcp.modelContextProtocolServer"); //$NON-NLS-1$
		
		for (IExtension extension : point.getExtensions()) {
			for (IConfigurationElement extensionElement: extension.getConfigurationElements()) {
				if ("contributor".equals(extensionElement.getName())) {
					Contributor c = new Contributor(extensionElement);
					if (c.errorMessage == null) {
						contributors.put(c.id, c);
					} else {
						trace(extensionElement, c.errorMessage, c.contributorThrowable);
					}
				}
			}
		}
	}
	
	public Contributor[] getContributors() {
		return contributors.values().toArray(Contributor[]::new);
	}
	
	public Contributor getContributor(String contributorId) {
		return contributors.get(contributorId);
	}
	
	public class Contributor {

		String id, name, description, provider;
		List<IFactory> factories;
		String errorMessage = null;
		Throwable contributorThrowable = null;

		Map<ResourceTemplate, SyncCompletionSpecification> templateCompletions = new HashMap<ResourceTemplate, SyncCompletionSpecification>();
		Map<ResourceTemplate, SyncResourceSpecification> templateSpecifications = new HashMap<ResourceTemplate, SyncResourceSpecification>();
		Map<Tool, SyncToolSpecification> toolSpecifications = new HashMap<Tool, SyncToolSpecification>();
		
		public Contributor(IConfigurationElement e) {
			this.id =  e.getAttribute("id"); 
			this.name = e.getAttribute("name");
			this.description = e.getAttribute("description");
			this.provider = e.getAttribute("provider");
			factories = new ArrayList<IFactory>();
			
			if (getId() == null || getId().isBlank()) {
				errorMessage = "Missing Tool id";
			} else if (getName() == null || getName().isBlank()) {
				errorMessage = "Missing Tool name";
			}
			
			if (errorMessage == null) {
				for (IConfigurationElement childElement: e.getChildren("factory")) {
					try {
						Object impl = childElement.createExecutableExtension("class");
						if (impl instanceof IFactory) {
							factories.add((IFactory)impl);
						} else {
							errorMessage = "Factory class " + e.getAttribute("class") + " not instanceof IMCPFactory";
						}
					} catch (CoreException ex) {
						errorMessage = "Factory class " + e.getAttribute("class") + "failed instantiation";
						contributorThrowable = ex;
					}
				}
			}
			
			if (errorMessage == null) {
				try {
					for (IFactory factory: factories) {
						if (factory instanceof IResourceFactory) {
							//TODO ?
						} else if (factory instanceof IResourceTemplateFactory) {
							IResourceTemplateFactory templateFactory = (IResourceTemplateFactory)factory;
							for (ResourceTemplate template: templateFactory.createResourceTemplates()) {
								templateCompletions.put(template, templateFactory.createCompletionSpecification(template));
								templateSpecifications.put(template, templateFactory.getResourceTemplateSpecification(template));
							}
						} else if (factory instanceof IToolFactory) {
							IToolFactory toolFactory = (IToolFactory)factory;
							Tool tool = toolFactory.createTool();
							SyncToolSpecification spec = toolFactory.createSpec(tool);
							toolSpecifications.put(tool, spec);
						}
					}
				} catch (Exception ex) {
					errorMessage = "Factory class " + e.getAttribute("class") + "failed setup";
					contributorThrowable = ex;
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

		public IFactory[] getFactories() {
			return factories.toArray(IFactory[]::new);
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