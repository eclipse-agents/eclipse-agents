package org.eclipse.mcp;

import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.ui.IWorkbenchPropertyPage;

public interface IMCPElementPropertyInput extends IWorkbenchPropertyPage {

	public String getId();
	public String getName();
	public String getCategory();
	public String getDescription();
	
	public DialogSettings loadCurrentSettings();
	public void applySettings(DialogSettings settings);
}
