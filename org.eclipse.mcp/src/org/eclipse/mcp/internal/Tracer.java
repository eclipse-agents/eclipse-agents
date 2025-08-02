package org.eclipse.mcp.internal;

/*******************************************************************************
Licensed Materials - Property of IBM
(C) Copyright IBM Corporation 2024. All Rights Reserved.
*
Note to U.S. Government Users Restricted Rights:
Use, duplication or disclosure restricted by GSA ADP Schedule
Contract with IBM Corp.
*******************************************************************************/

import java.util.Hashtable;

import org.eclipse.osgi.service.debug.DebugOptions;
import org.eclipse.osgi.service.debug.DebugOptionsListener;
import org.eclipse.osgi.service.debug.DebugTrace;
import org.osgi.framework.BundleContext;


public class Tracer implements DebugOptionsListener {


	public static final String DEBUG = "/debug";
	public static final String EXTENSION = "/debug/extension";
	
	public enum OPTION {
		DEBUG("/debug"),
		EXTENSION("/debug/extension");
		
		private String location;
		private String fullPath;
		private boolean isActive;
		
	    private OPTION(String location) {
	    	this.location = location;
	    	fullPath = Activator.PLUGIN_ID + location;
	    	isActive = false;
	    }

		public boolean isActive() {
			return isActive;
		}

		private void setActive(boolean isActive) {
			this.isActive = isActive;
		}

		public String getLocation() {
			return location;
		}

		public String getFullPath() {
			return fullPath;
		}

		@Override
		public String toString() {
			return getLocation();
		}
	}
	
	private static DebugTrace trace = null;
   private static Tracer instance = null;
	private static DebugTrace nullTrace = new DebugTrace() {
		@Override
		public void trace(String arg0, String arg1) {}
		@Override
		public void trace(String arg0, String arg1, Throwable arg2) {}
		@Override
		public void traceDumpStack(String arg0) {}
		@Override
		public void traceEntry(String arg0) {}
		@Override
		public void traceEntry(String arg0, Object arg1) {}
		@Override
		public void traceEntry(String arg0, Object[] arg1) {}
		@Override
		public void traceExit(String arg0) {}
		@Override
		public void traceExit(String arg0, Object arg1) {}
	
	};

   private Tracer(BundleContext context) {
	   Hashtable props = new Hashtable(4);
       props.put(DebugOptions.LISTENER_SYMBOLICNAME, Activator.PLUGIN_ID);
       context.registerService(DebugOptionsListener.class.getName(), this, props);
	}

	public static void setup(BundleContext context) {
		if (instance == null) {
			instance = new Tracer(context);
		}
	}
	
	@Override
	public void optionsChanged(DebugOptions options) {

		for (OPTION level: OPTION.values()) {
			level.setActive(options.getBooleanOption(level.getFullPath(), false));
		}
		if (trace == null) {
			trace = options.newDebugTrace(Activator.PLUGIN_ID);
		}
		trace.trace(OPTION.DEBUG.getLocation(), toString());
} 

	// used to bypass eclipse dependencies during unit testing
	public static boolean disableTracing = false;	

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (OPTION level: OPTION.values()) {
			sb.append(level.getLocation() + ": " + level.isActive() + "; ");
		}
		return sb.toString();
	}
	
	public static DebugTrace trace() {
		if (trace == null || disableTracing) {
			return nullTrace;
		}
		return trace;
	}
}