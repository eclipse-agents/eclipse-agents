package com.ibm.systemz.mcp.mvs.job;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.services.files.RemoteFileException;

import com.ibm.ftt.resource.utils.PBResourceUtils;
import com.ibm.ftt.resources.zos.PBResourceMvsUtils;
import com.ibm.ftt.resources.zos.filesystem.IMVSResource;
import com.ibm.ftt.resources.zos.filesystem.IPartitionedDataSet;
import com.ibm.ftt.resources.zos.zosphysical.IZOSCatalog;
import com.ibm.ftt.resources.zos.zosphysical.IZOSDataSetMember;
import com.ibm.ftt.resources.zos.zosphysical.IZOSPartitionedDataSet;
import com.ibm.ftt.resources.zos.zosphysical.IZOSSystemImage;
import com.ibm.ftt.resources.zos.zosphysical.impl.ZOSCatalog;
import com.ibm.ftt.resources.zos.zosphysical.impl.ZOSPartitionedDataSet;
import com.ibm.ftt.rse.mvs.util.IMVSConstants;
import com.ibm.systemz.mcp.mvs.Activator;



/**
 * See: com.ibm.ftt.rse.mvs.client.ui.dialogs.QueryDataSetsJob
 */
public class QueryPDSMemberJob extends Job {

	String dataSetName;
	String dataSetMemberFilter;
	
	ISubSystem subSystem;
	
	int PREF_DEFAULT_MVS_FIND_MEMBER_LIMIT = 100;
	
	// used by child job
	List<IZOSDataSetMember> members;
	
	public QueryPDSMemberJob(ISubSystem subSystem){
		super("Model Context Protocol (MVS)");

		this.subSystem = subSystem;
		this.members = new ArrayList<IZOSDataSetMember>();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		members.clear();

		monitor.beginTask("Searching for " + dataSetName + "(" + dataSetMemberFilter + ")", IProgressMonitor.UNKNOWN);
		
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
		catalog.setStale(true);
		IAdaptable dataSet = ((ZOSCatalog) catalog).findMember(dataSetName);
			
		if (dataSet != null) {
			if (dataSet instanceof IZOSPartitionedDataSet) {
				IZOSPartitionedDataSet pds = (IZOSPartitionedDataSet)dataSet;
				int memberCount = 0;
				pds.setStale(true);					
				IMVSResource mvsRes = ((ZOSPartitionedDataSet)pds).getMvsResource();
				if (mvsRes instanceof IPartitionedDataSet) {
					try {
						memberCount = ((IPartitionedDataSet)mvsRes).queryMembersCount(dataSetMemberFilter, null);
					} catch (RemoteFileException e) {
						return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "FindMemberDialog#refreshPressed() Remote File Exception getting data set count.", e); //$NON-NLS-1$
					} catch (InterruptedException e) {
						return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "FindMemberDialog#refreshPressed() Interrupted Exception getting data set count.", e); //$NON-NLS-1$
					}
	
//					Trace.trace(this, UiPlugin.TRACE_ID, Trace.FINEST,"FindMemberDialog#refreshPressed() Member Count = " + memberCount); //$NON-NLS-1$
	
					// Get the member count threshold from preferences
					int memberCountThreshold = IMVSConstants.PREF_DEFAULT_MVS_FIND_MEMBER_LIMIT;
					IPreferenceStore store = PBResourceMvsUtils.getPreferenceStore();
					memberCountThreshold = store.getInt(IMVSConstants.PREF_MVS_FIND_MEMBER_LIMIT);
					
					if (memberCount > memberCountThreshold) {
						return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Too many data set members match pattern" + memberCount);
					}
				}					
										
				IAdaptable[] adapables = pds.members(dataSetMemberFilter, true);
				if (adapables != null && adapables.length > 0) {

					for (IAdaptable adaptable: adapables) {
						if (adaptable instanceof IZOSDataSetMember) {
							IZOSDataSetMember member = (IZOSDataSetMember)adaptable;
							members.add(member);
						}
					}
				}
			}
		} else {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Data set not found: " + dataSetName);
		}

		return Status.OK_STATUS;
	}
 	

 	public String getDataSetName() {
		return dataSetName;
	}
 	
 	public void setDataSetName(String dataSetName) {
 		this.dataSetName = dataSetName.toUpperCase();
 	}

	public String getDataSetMemberFilter() {
		return dataSetMemberFilter;
	}
	
 	public void setDataSetMemberFilter(String dataSetMemberFilter) {
 		this.dataSetMemberFilter = dataSetMemberFilter.toUpperCase();
 	}
	
	public List<IZOSDataSetMember> getMembers() {
		return members;
	}

}
