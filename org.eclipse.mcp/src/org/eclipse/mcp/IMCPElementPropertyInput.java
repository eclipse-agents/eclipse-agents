package org.eclipse.mcp;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.DialogSettings;

public interface IMCPElementPropertyInput extends IAdaptable {

	public String getServerId();
	public String getElementId();
	
	public DialogSettings loadCurrentSettings(String propertyEditorId);
	public void applySettings(String propertyEditorId, DialogSettings settings);
}
