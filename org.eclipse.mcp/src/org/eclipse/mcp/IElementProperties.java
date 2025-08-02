package org.eclipse.mcp;

import org.eclipse.jface.dialogs.IDialogSettings;

public interface IElementProperties {

	/**
	 * Returns an IDialoSettings holding any customized properties for the property page id associated with this element
	 * @param propertyPageId id of a property page associated with this server element (Tool, Resource Manager)
	 * @return returns an IDialoSettings holding any customized properties for the
	 */
	public IDialogSettings getProperties(String propertyPageId) ;
	
	/**
	 * Open property dialog with the property pages associated with this element
	 * @param selectedPageId the property page to initially select out of those associated with this element
	 */
	public void openPropertiesEditor(String selectedPageId);
		
}
