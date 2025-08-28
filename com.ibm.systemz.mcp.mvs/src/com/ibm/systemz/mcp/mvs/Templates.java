package com.ibm.systemz.mcp.mvs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mcp.MCPException;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.SystemStartHere;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.springaicommunity.mcp.annotation.McpComplete;
import org.springaicommunity.mcp.annotation.McpResource;

import com.ibm.ftt.resources.zos.zosphysical.IZOSDataSetMember;
import com.ibm.ftt.rse.mvs.client.subsystems.IMVSFileSubSystem;
import com.ibm.systemz.mcp.mvs.job.QueryDataSetsJob;
import com.ibm.systemz.mcp.mvs.job.QueryPDSMemberJob;

import io.modelcontextprotocol.spec.McpSchema.CompleteRequest.CompleteArgument;
import io.modelcontextprotocol.spec.McpSchema.CompleteRequest.CompleteContext;

public class Templates {

	Map<ISubSystem, QueryDataSetsJob> dataSetSearchJobs = new HashMap<ISubSystem, QueryDataSetsJob>();
	Map<ISubSystem, String> dataSetSearchFilters = new HashMap<ISubSystem, String>();
	
	Map<ISubSystem, QueryPDSMemberJob> pdsMemberSearchJobs = new HashMap<ISubSystem, QueryPDSMemberJob>();
	Map<ISubSystem, String> pdsMemberSearchFilters = new HashMap<ISubSystem, String>();
	
	
	public Templates() {
		
	}

    @McpResource(
    		uri = "file://mvs/{host}/{pds}/{member}", 
            name = "PDS Member", 
            description = "A file that is a member of a an IBM System z Multiple Virtual Storage(MVS) Partitioned Data Set (PDS)")
	public String getPDSMemberContent(String host, String pds, String member) {		
		return new MvsResourceAdapter().uriToResourceContent("file://mvs/" + host + "/" + pds + "/" + member);
	}
    
    @McpComplete(uri = "file://mvs/{host}/{pds}/{member}")
   	public List<String> completeRelativePath(
   			CompleteArgument argument, CompleteContext context) {
    	
List<String> result = new ArrayList<String>();
		
		if (argument.name().equals("host")) {
		
			for (IHost host : SystemStartHere.getConnections()) {
				if (host.getSystemType().getId().equals("com.ibm.etools.zos.system")) { //$NON-NLS-1$
					result.add(host.getHostName());
				}
			}
		} else if (argument.name().equals("pds")) {
			
			ISubSystem subSystem = findMvsSubsystem(context.arguments().get("host"));
			
			if (subSystem != null) {
				dataSetSearchFilters.put(subSystem, argument.value());

				if (!dataSetSearchJobs.containsKey(subSystem)) {
					dataSetSearchJobs.put(subSystem, new QueryDataSetsJob(subSystem));
				}
				
				//TODO should we add some throttling here?
				if (dataSetSearchJobs.get(subSystem).getState() == Job.RUNNING) {
					dataSetSearchJobs.get(subSystem).cancel();
				}
				
				dataSetSearchJobs.get(subSystem).setFilter(argument.value());
				dataSetSearchJobs.get(subSystem).schedule();
				try {
					dataSetSearchJobs.get(subSystem).join();
				} catch (InterruptedException e) {
					System.out.println(e.getLocalizedMessage());
					throw new MCPException(e);
				}
				
				if (dataSetSearchJobs.get(subSystem).getResult().isOK()) {
					result.addAll(dataSetSearchJobs.get(subSystem).getResults());
				} else if (dataSetSearchJobs.get(subSystem).getResult().getSeverity() == IStatus.ERROR) {
					throw new MCPException(dataSetSearchJobs.get(subSystem).getResult());
				}
			}
		} else if (argument.name().equals("member")) {

			ISubSystem subSystem = findMvsSubsystem(context.arguments().get("host"));
			
			if (subSystem != null) {
				pdsMemberSearchFilters.put(subSystem, argument.value());

				if (!pdsMemberSearchJobs.containsKey(subSystem)) {
					pdsMemberSearchJobs.put(subSystem, new QueryPDSMemberJob(subSystem));
				}
				
				//TODO should we add some throttling here?
				if (pdsMemberSearchJobs.get(subSystem).getState() == Job.RUNNING) {
					pdsMemberSearchJobs.get(subSystem).cancel();
				}
				
				pdsMemberSearchJobs.get(subSystem).setDataSetName(context.arguments().get("pds"));
				if (argument.value().endsWith("*")) {
					pdsMemberSearchJobs.get(subSystem).setDataSetMemberFilter(argument.value());
				} else {
					pdsMemberSearchJobs.get(subSystem).setDataSetMemberFilter(argument.value() + "*");
				}
				
				pdsMemberSearchJobs.get(subSystem).schedule();
				try {
					pdsMemberSearchJobs.get(subSystem).join();
				} catch (InterruptedException e) {
					throw new MCPException(e);
				}
				
				if (pdsMemberSearchJobs.get(subSystem).getResult().isOK()) {
					for (IZOSDataSetMember member: pdsMemberSearchJobs.get(subSystem).getMembers()) {
						result.add(member.getName());
					}
					
				} else if (pdsMemberSearchJobs.get(subSystem).getResult().getSeverity() == IStatus.ERROR) {
					throw new MCPException(pdsMemberSearchJobs.get(subSystem).getResult());
				}
			}
		}
		return result;
   	}
    
    private ISubSystem findMvsSubsystem(String systemName) {
		for (IHost host : SystemStartHere.getConnections()) {
			if (host.getSystemType().getId().equals("com.ibm.etools.zos.system")) { //$NON-NLS-1$
				if (host.getHostName().equals(systemName)) {

					for (ISubSystem subSystem: host.getSubSystems()) {
						if (subSystem instanceof IMVSFileSubSystem) {
							return subSystem;
						}
					}
				}
			}
		}
		return null;
	}

}
