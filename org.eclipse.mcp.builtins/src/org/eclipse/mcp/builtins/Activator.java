package org.eclipse.mcp.builtins;

import org.eclipse.mcp.builtin.resource.AbsoluteFileAdapter;
import org.eclipse.mcp.builtin.resource.ConsoleAdapter;
import org.eclipse.mcp.builtin.resource.EditorAdapter;
import org.eclipse.mcp.builtin.resource.IResourceAdapter;
import org.eclipse.mcp.builtin.resource.RelativeFileAdapter;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.mcp.builtins"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private IResourceAdapter<?>[] adapters = new IResourceAdapter[] {
		new RelativeFileAdapter(),
		new AbsoluteFileAdapter(),
		new EditorAdapter(),
		new ConsoleAdapter()
	};
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	public String getResourceContent(String uri) {
		for (IResourceAdapter<?> adapter: adapters) {
			if (uri.startsWith(adapter.getUniqueTemplatePrefix())) {
				return adapter.uriToResourceContent(uri);
			}
		}
		return null;
	}
	
	public Object getEclipseResource(String uri) {
		for (IResourceAdapter<?> adapter: adapters) {
			if (uri.startsWith(adapter.getUniqueTemplatePrefix())) {
				return adapter.uriToEclipseObject(uri);
			}
		}
		return null;
	}
}
