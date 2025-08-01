package org.eclipse.mcp;

import org.eclipse.jface.dialogs.IDialogSettings;

public interface IElementProperties {

	public IDialogSettings getProperties(String propertyPageId) ;
	
	public void openPropertiesEditor(String selectedPageId);
		
}
