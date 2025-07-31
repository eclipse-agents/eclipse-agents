package org.eclipse.mcp.internal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.mcp.Activator;
import org.eclipse.mcp.internal.preferences.ExtensionServer;
import org.eclipse.mcp.internal.preferences.IPreferencedServer;

public class PreferenceManager {

	public static final String fileName = "settings.txt";
	public static final String EXTENSION_SERVERS = "extensionServers";
	public static final String USER_SERVERS = "userServers";
	

	List<IPreferencedServer> servers = new ArrayList<IPreferencedServer>();

	public PreferenceManager() {
	}

	public void load() {
		servers.clear();
		
		IPath path = Activator.getDefault().getStateLocation();
		String filename = path.append(fileName).toOSString();
		DialogSettings serversSettings = new DialogSettings("root");
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
						servers.add(new ExtensionServer(server, matchingPreferences, (DialogSettings)section));
					}
				}
			} else if (USER_SERVERS.equals(section.getName())) {
				
			}
		}
	}
	
	public IPreferencedServer[] getServers() {
		return servers.toArray(new IPreferencedServer[0]);
	}
}
