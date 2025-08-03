package org.eclipse.mcp.internal;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.mcp.IElementProperties;
import org.eclipse.mcp.IMCPElementPropertyInput;
import org.eclipse.mcp.internal.preferences.IPreferencedServer;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class ElementProperties implements IElementProperties {

	private String serverId;
	private String elementId;
	private String elementName;
	private String elementImageId;
	private String[] propertyEditorIds;
	
	public ElementProperties(String serverId, String elementId, String elementName, String elementImageId, String[] propertyEditorIds) {
		super();
		this.serverId = serverId;
		this.elementId = elementId;
		this.elementName = elementName;
		this. elementImageId = elementImageId;
		this.propertyEditorIds = propertyEditorIds;
	}

	public IDialogSettings getProperties(String propertyPageId) {
		PreferenceManager preferenceManager = new PreferenceManager();
		preferenceManager.load();
		IPreferencedServer server  = preferenceManager.getServer(serverId);
		if (server != null) {
			
			for (String propertyEditorId: propertyEditorIds) {
				if (propertyPageId.equals(propertyEditorId)) {
					return server.getElementSettings(elementId, propertyPageId);
				}
			}
			// TRACE
		}
		
		return null;
	}
	
	public void openPropertiesEditor(String selectedPageId) {
		PreferenceManager preferenceManager = new PreferenceManager();
		preferenceManager.load();
		IMCPElementPropertyInput input = new MCPElementPropertyInput(serverId, elementId, elementName, elementImageId, null);
			
		PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(
					Activator.getDisplay().getActiveShell(), input,
					selectedPageId,
					propertyEditorIds,
					input);
		
		if (dialog != null) {
			dialog.open();
		}
		
	}
	
}
