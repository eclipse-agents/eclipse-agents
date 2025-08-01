package org.eclipse.mcp;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.ui.dialogs.PropertyPage;

public interface IMCPElementPropertyInput extends IAdaptable {

	public String getServerId();
	public String getElementId();
	
	public DialogSettings loadCurrentSettings(PropertyPage page);
	public void applySettings(PropertyPage page, DialogSettings settings);
}
