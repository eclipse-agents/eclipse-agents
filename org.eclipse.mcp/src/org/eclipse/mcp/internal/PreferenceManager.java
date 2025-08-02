package org.eclipse.mcp.internal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mcp.IMCPElementPropertyInput;
import org.eclipse.mcp.internal.preferences.ExtensionServer;
import org.eclipse.mcp.internal.preferences.IPreferencedServer;
import org.eclipse.ui.dialogs.PropertyPage;

public class PreferenceManager {

	public static final String fileName = "settings.txt";
	public static final String EXTENSION_SERVERS = "extensionServers";
	public static final String USER_SERVERS = "userServers";
	

	List<IPreferencedServer> servers = new ArrayList<IPreferencedServer>();
	DialogSettings serversSettings;
	String filename;

	public PreferenceManager() {
		IPath path = Activator.getDefault().getStateLocation();
		filename = path.append(fileName).toOSString();
		serversSettings = new DialogSettings("root");
		try {
			serversSettings.load(filename);
		} catch (IOException e) {
			if (e instanceof FileNotFoundException) {
				try {
					serversSettings.addNewSection(EXTENSION_SERVERS);
					serversSettings.addNewSection(USER_SERVERS);
					serversSettings.save(filename);
					serversSettings.load(filename);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
	}

	public void load() {
		servers.clear();

		if (serversSettings == null) {
			return;
		}

		for (IDialogSettings section : serversSettings.getSections()) {
			if (EXTENSION_SERVERS.equals(section.getName())) {
				for (ExtensionManager.Server server: Activator.getDefault().getExtensionManager().getServers()) {
					if (server.getId() != null) {
						DialogSettings matchingPreferences = null;
						for (IDialogSettings extensionPreferences : section.getSections()) {
							if (server.getId().equals(extensionPreferences.get(IPreferencedServer.ID))) {
								matchingPreferences = (DialogSettings)extensionPreferences;
								break;
							}
						}
						if (matchingPreferences == null) {
							matchingPreferences = (DialogSettings)section.addNewSection(server.getId());
						}
						servers.add(new ExtensionServer(server, matchingPreferences, (DialogSettings)section));
					}
				}
			} else if (USER_SERVERS.equals(section.getName())) {
				
			}
		}
	}
	
	public void save() {
		try {
			serversSettings.save(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public IPreferencedServer[] getServers() {
		return servers.toArray(new IPreferencedServer[0]);
	}
	
	public IPreferencedServer getServer(String id) {
		for (IPreferencedServer server: servers) {
			if (server.getId().equals(id)) {
				return server;
			}
		}
		return null;
	}
	
	public  IMCPElementPropertyInput getElementPropertyInput(String serverId, String elementId) {

		return new IMCPElementPropertyInput() {

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
				if (propertyEditorId != null) {
					for (IPreferencedServer server: servers) {
						if (server.getId().equals(serverId)) {
							return server.getElementSettings(elementId, propertyEditorId);
						}
					}
				}
				return new DialogSettings(propertyEditorId);
			}

			@Override
			public void applySettings(PropertyPage page, DialogSettings settings) {
				String propertyEditorId = getPropertyPageId(page);
				for (IPreferencedServer server: servers) {
					if (server.getId().equals(serverId)) {
						server.setElementSettings(elementId, propertyEditorId, settings);
						save();
						break;
					}
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
		};
	}
}
