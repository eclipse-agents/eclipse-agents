package org.eclipse.mcp.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mcp.Activator;
import org.eclipse.mcp.factory.IFactory;
import org.eclipse.mcp.internal.ExtensionManager.Contributor;
import org.eclipse.mcp.internal.preferences.IPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.ActivityManagerEvent;
import org.eclipse.ui.activities.IActivity;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IActivityManagerListener;

import io.modelcontextprotocol.spec.McpSchema.LoggingLevel;

public class ServerManager implements IPreferenceConstants, IActivityManagerListener {

	private ManagedServer server = null;
	private String name, description;
	
	Set<String> activityIds;
	
	public ServerManager() {
		
		PlatformUI.getWorkbench().getActivitySupport().getActivityManager().addActivityManagerListener(this);
		name = "Eclipse MCP Server";
		description = "Default Eclipse MCP Server";
		activityIds = new HashSet<String>();
		start();

	}
	
	public void start() {
		Tracer.trace().trace(Tracer.DEBUG, "Starting"); //$NON-NLS-1$
		Set<Contributor> contributors = new HashSet<Contributor>();
		activityIds.clear();
		server = null;

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		IActivityManager activites = PlatformUI.getWorkbench().getActivitySupport().getActivityManager();
		if (store.getBoolean(P_SERVER_ENABLED)) {
			int port = store.getInt(P_SERVER_HTTP_PORT);
			
			List<IFactory> factories = new ArrayList<IFactory>();
			
			for (ExtensionManager.Contributor contributor: Activator.getDefault().getExtensionManager().getContributors()) {
				if (contributor.getActivityId() == null) {
					contributors.add(contributor);
				} else {
					IActivity activity = activites.getActivity(contributor.getActivityId());
					if (activity != null && activity.isDefined() && activity.isEnabled()) {
						contributors.add(contributor);
						activityIds.add(activity.getId());
					}
				}
			}
			
			for (Contributor contributor: contributors) {
				factories.addAll(Arrays.asList(contributor.getFactories()));
			}
			
			server = new ManagedServer(name, description, port, factories.toArray(IFactory[]::new));
			server.start();
		};
		
	}
	
	public void stop() {
		Tracer.trace().trace(Tracer.DEBUG, "Stopping"); //$NON-NLS-1$
		if (server != null) {
			server.stop();
		}
	}
	
	public void forceRestart() {
		stop();
		start();
	}
	

	public void log(String message, Throwable error) {
		if (message != null) {
			server.log(LoggingLevel.INFO, this, message);
		}

		if (error != null) {
			server.log(error);
		}
	}
	
	public String getResourceContent(String uri) {
		return server.getResourceContent(uri);
	}
	
	public Object getEclipseResource(String uri) {
		return server.getEclipseResource(uri);
	}	

	@Override
	public void activityManagerChanged(ActivityManagerEvent event) {
		if (event.haveEnabledActivityIdsChanged()) {
			for (String oldActivityId: event.getPreviouslyEnabledActivityIds()) {
				if (!event.getActivityManager().getEnabledActivityIds().contains(oldActivityId)) {
					// oldActivityId was disabled/removed
					if (activityIds.contains(oldActivityId)) {
						Tracer.trace().trace(Tracer.DEBUG, "Activity Disabled: " + oldActivityId); //$NON-NLS-1$
						forceRestart();
						return;
					}
				}
			}
			
			
			for (String newActivityId: event.getActivityManager().getEnabledActivityIds()) {
				if (!event.getPreviouslyEnabledActivityIds().contains(newActivityId)) {
					// newctivityId was enabled/added
					if (!activityIds.contains(newActivityId)) {
						Tracer.trace().trace(Tracer.DEBUG, "Activity Enabled: " + newActivityId); //$NON-NLS-1$
						forceRestart();
						return;
					}
				}
			}
		}
	}
}
