package com.ibm.systemz.mcp.mvs.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.services.files.RemoteFileException;

import com.ibm.ftt.resource.utils.PBResourceUtils;
import com.ibm.ftt.resources.bridge.ResourcesBridgeFactory;
import com.ibm.ftt.resources.zos.filesystem.IHLQ;
import com.ibm.ftt.resources.zos.filesystem.impl.HLQ;
import com.ibm.ftt.resources.zos.zosphysical.IZOSCatalog;
import com.ibm.ftt.resources.zos.zosphysical.IZOSDataSet;
import com.ibm.ftt.resources.zos.zosphysical.IZOSSystemImage;
import com.ibm.ftt.resources.zos.zosphysical.impl.ZOSCatalog;
import com.ibm.systemz.mcp.mvs.Activator;



/**
 * See: com.ibm.ftt.rse.mvs.client.ui.dialogs.QueryDataSetsJob
 */
public class QueryDataSetsJob extends Job {

	String filter;
	ISubSystem subSystem;
	
	int DATASET_COUNT_THRESHOLD = 100;
	List<String> results;
	
	public QueryDataSetsJob(ISubSystem subSystem){
		super("Model Context Protocol (MVS)");

		this.subSystem = subSystem;
		this.results = new ArrayList<String>();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		results.clear();
		String pattern = filter;
		try {
			subSystem.checkIsConnected(monitor);
		} catch (SystemMessageException e) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e);
		}
		
		if (monitor.isCanceled()) {
			return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Connection cancelled...");
		}
		
		IZOSSystemImage currentSystem = (IZOSSystemImage)PBResourceUtils.findMVSSystem((ISubSystem)subSystem);
		if (currentSystem == null) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "IZOSSystemImage not found for " + subSystem.getName());
		}
		IZOSCatalog catalog = (IZOSCatalog) currentSystem.getRoot();
		
		// Get the data set count
		String hlqName = "";
		IHLQ hlq = null;
		
		//TODO
		if (pattern.indexOf(".") < 0) {
			pattern = subSystem.getUserId().toUpperCase() + "." + pattern;
		} 
		if (!pattern.endsWith("*")) {
			pattern += "*";
		}
		
		monitor.beginTask("Searching for PDS: " + pattern, IProgressMonitor.UNKNOWN);
		
		int pos = pattern.indexOf("."); 
		if ( pos > -1) {
			hlqName = pattern.substring(0, pos);
		}
		
		Vector<?> hlqList = ((ZOSCatalog)catalog).getHLQList();		
		synchronized (hlqList) {
			hlq = getHLQ(hlqList, hlqName);
		}
		
		if (hlq == null) {
			ResourcesBridgeFactory.getSingleton().loadCatalog(currentSystem, pattern, monitor);
			hlqList = ((ZOSCatalog)catalog).getHLQList();
			synchronized (hlqList) {
				hlq = getHLQ(hlqList, hlqName);
			}
		}
		
		/**
		 * DKM: This was used before to check if the number of datasets excess a threshold (i.e. 1000).
		 * Now that the query happens in a job rather than the main thread, we shouldn't have to impose this arbitrary restriction.
		 */
		//TODO
		int dataSetCount = 0;
		if(hlq != null){
			try {
				dataSetCount = ((HLQ)hlq).queryDataSetsCount(pattern , monitor);
			} catch (RemoteFileException e) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "RetrieveDataSetDialog#refreshPressed() Remote File Exception getting data set count.", e);
			} catch (InterruptedException e) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "RetrieveDataSetDialog#refreshPressed() Interrupted Exception getting data set count.", e); //$NON-NLS-1$
			}
		} else {	
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "RetrieveDataSetDialog#refreshPressed() - no hlq match for hlqName " + hlqName); //$NON-NLS-1$
		}
		
		if (dataSetCount > DATASET_COUNT_THRESHOLD) {
			final int fdataSetCount = dataSetCount;
			return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Too many data sets match pattern" + dataSetCount); 
		}

		
		catalog.setStale(true);
		IAdaptable[] members = ((ZOSCatalog) catalog).members(pattern, monitor); // need to use a monitor here!

		if (monitor.isCanceled()) {
			return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Cancelled");
		}
		for (IAdaptable member: members) {
			if (member instanceof IZOSDataSet) {
				results.add(((IZOSDataSet)member).getName());
			}
		}

		return Status.OK_STATUS;
	}
	
 	private IHLQ getHLQ(Vector<?> hlqList, String hlqName) {
  
  		IHLQ hlq = null;
  
  		if (hlqList != null) {
  			for (int i = 0; i < hlqList.size(); i++){
  				hlq = (IHLQ)hlqList.elementAt(i);
  				if(hlqName.equalsIgnoreCase(hlq.getName())){
  					return hlq;
  				}
  			}
  		}
  		return null;
  	}
 	
 	public void setFilter(String filter) {
 		this.filter = filter.toUpperCase();
 	}

 	public List<String> getResults() {
 		return results;
 	}

}
