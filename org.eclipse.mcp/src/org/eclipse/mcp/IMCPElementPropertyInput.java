package org.eclipse.mcp;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Tools and Resource Managers declared in extension point <code>org.eclipse.mcp.modelContextProtocolServer</code>
 *  can have 1 or more <code>propertyPage</bode> elements declared.  These are ids of an <code>org.eclipse.ui.propertyPages</code>
 *  extension and can be used to enable end-users to customize preferences and behaviors of a Tool or Resource Manager for a particular server.
 *  
 * When a user edits a servers Tool or Resource Manager, any declared <code>propertyPage</bode> will be loaded in a properties editor.
 * 
 * Your implementations of <code>org.eclipse.ui.propertyPages</code> can call  <code>IAdaptable org.eclipse.ui.dialogs.PropertyPage.getElement()</code> to fetch the
 * <code>IMCPElementPropertyInput</code> the dialog was opened on.
 * 
 * Your propertyPage can use the <code>IMCPElementPropertyInput</code> to load and save any applied user changes.
 *  
 */
public interface IMCPElementPropertyInput extends IAdaptable {

	/**
	 * @return the MCP server id
	 */
	public String getServerId();
	
	/**
	 * 
	 * @return the id of a server tool or resource manager
	 */
	public String getElementId();
	
	/**
	 * 
	 * @param page A custom property page associated with an MCP Server's tool or Resource manager 
	 * @return a DialogSettings holding the currently saved values for the page argument
	 */
	public DialogSettings loadCurrentSettings(PropertyPage page);
	
	/**
	 * 
	 * @param page A custom property page associated with an MCP Server's tool or Resource manager
	 * @param settings a DialogSettings holding values a user is applying from page argument
	 */
	public void applySettings(PropertyPage page, DialogSettings settings);
}
