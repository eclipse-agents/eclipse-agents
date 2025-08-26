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

		String id, name, description, provider, activityId;
		List<IFactory> factories;
		String errorMessage = null;
		Throwable contributorThrowable = null;

		public Contributor(IConfigurationElement e) {
			this.id =  e.getAttribute("id"); 
			this.name = e.getAttribute("name");
			this.description = e.getAttribute("description");
			this.provider = e.getAttribute("provider");
			this.activityId = e.getAttribute("activityId");
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
		
		public String getActivityId() {
			return activityId;
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
		
		String output = "[" + identifier +"]:: " + message;
		
		if (t != null) {
			Tracer.trace().trace(Tracer.IMPLEMENTATIONS, output, t);
		} else {
			Tracer.trace().trace(Tracer.IMPLEMENTATIONS, output);
		}
	}
}