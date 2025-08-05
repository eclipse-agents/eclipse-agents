package org.eclipse.mcp.internal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.mcp.internal.preferences.ExtensionServer;
import org.eclipse.mcp.internal.preferences.IPreferencedServer;

public class PreferenceManager {

	public static final String fileName = "settings.txt";

	// servers declared via extension point
	public static final String EXTENSION_SERVERS = "extensionServers";
	
	//TODO <unimplemented> servers created by user preferences 
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
				for (IDialogSettings extensionServerPreferences : section.getSections()) {
					ExtensionManager.Server server = Activator.getDefault().getExtensionManager().getServer(extensionServerPreferences.getName());
					if (server != null) {
						servers.add(new ExtensionServer(server, (DialogSettings)extensionServerPreferences, (DialogSettings)section));
					}
				}
				for (ExtensionManager.Server server: Activator.getDefault().getExtensionManager().getServers()) {
					boolean found = false;
					for (IPreferencedServer preferencedServer: servers) {
						if (server.getId().equals(preferencedServer.getId())) {
							found = true;
							break;
						}
					}
					if (!found) {
						IDialogSettings emptyServerSettings = (DialogSettings)section.addNewSection(server.getId());
						servers.add(new ExtensionServer(server, (DialogSettings)emptyServerSettings, (DialogSettings)section));
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
	
	public void dispose() {
		
	}
}
