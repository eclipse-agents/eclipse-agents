package org.eclipse.mcp.internal;

import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mcp.IMCPElementPropertyInput;
import org.eclipse.mcp.internal.preferences.IPreferencedServer;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class MCPElementPropertyInput implements IMCPElementPropertyInput, IWorkbenchAdapter {

	//If not null, then defer to this for get/set of values
	//Other wise get values straight from file
	PreferenceManager _pm;
	String serverId, elementId, elementName, elementImageId;
	
	
	public MCPElementPropertyInput(String serverId, String elementId, String elementName, String elementImageId, PreferenceManager preferenceManager) {
		super();
		this.serverId = serverId;
		this.elementId = elementId;
		this.elementName = elementName;
		this.elementImageId = elementImageId;
		this._pm = preferenceManager;
	}

	@Override
	public String getServerId() {
		return serverId;
	}

	@Override
	public String getElementId() {
		return elementId;
	}

	@Override
	public DialogSettings loadCurrentSettings(PropertyPage page) {
		String propertyEditorId = getPropertyPageId(page);
		PreferenceManager preferenceManager = getPreferenceManager();
		
		if (propertyEditorId != null) {
			IPreferencedServer server = preferenceManager.getServer(serverId);
			if (server != null) {
				return server.getElementSettings(elementId, propertyEditorId);
			}
		}
		return new DialogSettings(propertyEditorId);
	}

	@Override
	public void applySettings(PropertyPage page, DialogSettings settings) {
		String propertyEditorId = getPropertyPageId(page);
		PreferenceManager preferenceManager = getPreferenceManager();
		
		IPreferencedServer server = preferenceManager.getServer(serverId);
		if (server != null) {
			server.setElementSettings(elementId, propertyEditorId, settings);
			preferenceManager.save();
		}
	}

	@Override
	public <T> T getAdapter(Class<T> arg0) {
		return null;
	}
	
	public String getPropertyPageId(PropertyPage page) {
		if (page.getContainer() instanceof PreferenceDialog) {
			PreferenceDialog dialog = (PreferenceDialog)page.getContainer();
			ISelection selection = dialog.getTreeViewer().getSelection();
			if (selection instanceof IStructuredSelection) {
				Object element = ((IStructuredSelection)selection).getFirstElement();
				if (element instanceof PreferenceNode) {
					return ((PreferenceNode)element).getId();
				}
			}
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object arg0) {
		return null;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object arg0) {
		return Activator.getDefault().getImageRegistry().getDescriptor(elementImageId);
	}

	@Override
	public String getLabel(Object arg0) {
		return elementName;
	}

	@Override
	public Object getParent(Object arg0) {
		return null;
	}
	
	private PreferenceManager getPreferenceManager() {
		if (_pm == null) {
			PreferenceManager manager = new PreferenceManager();
			manager.load();
			return manager;
		}
		return _pm;
	}

}
