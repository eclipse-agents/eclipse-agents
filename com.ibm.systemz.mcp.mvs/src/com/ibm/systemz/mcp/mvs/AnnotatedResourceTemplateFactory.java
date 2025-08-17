package com.ibm.systemz.mcp.mvs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mcp.MCPException;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedResourceTemplateFactory;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedResourceTemplateFactory.ResourceTemplate;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.SystemStartHere;
import org.eclipse.rse.core.subsystems.ISubSystem;

import com.ibm.ftt.rse.mvs.client.subsystems.IMVSFileSubSystem;
import com.ibm.systemz.mcp.mvs.job.FetchPDSMemberContent;
import com.ibm.systemz.mcp.mvs.job.QueryDataSetsJob;
import com.ibm.systemz.mcp.mvs.job.QueryPDSMemberJob;



@ResourceTemplate (name = "PDS Member", 
		description = "A file that is a member of a an IBM System z Multiple Virtual Storage(MVS) Partitioned Data Set (PDS)",
		uriTemplate = "file://{host}/mvs/{pds}/{member}")
public class AnnotatedResourceTemplateFactory extends MCPAnnotatedResourceTemplateFactory {

	Map<ISubSystem, QueryDataSetsJob> dataSetSearchJobs = new HashMap<ISubSystem, QueryDataSetsJob>();
	Map<ISubSystem, String> dataSetSearchFilters = new HashMap<ISubSystem, String>();
	
	Map<ISubSystem, QueryPDSMemberJob> pdsMemberSearchJobs = new HashMap<ISubSystem, QueryPDSMemberJob>();
	Map<ISubSystem, String> pdsMemberSearchFilters = new HashMap<ISubSystem, String>();
	
	
	@Override
	public List<String> completionReq(String argumentName, String argumentValue, String uri, Map<String, String> arguments) {
		List<String> result = new ArrayList<String>();
		
		if (argumentName.equals("host")) {
		
			for (IHost host : SystemStartHere.getConnections()) {
				if (host.getSystemType().getId().equals("com.ibm.etools.zos.system")) { //$NON-NLS-1$
					result.add(host.getHostName());
				}
			}
		} else if (argumentName.equals("pds")) {
			
			ISubSystem subSystem = findMvsSubsystem(arguments.get("host"));
			
			if (subSystem != null) {
				dataSetSearchFilters.put(subSystem, argumentValue);

				if (!dataSetSearchJobs.containsKey(subSystem)) {
					dataSetSearchJobs.put(subSystem, new QueryDataSetsJob(subSystem));
				}
				
				//TODO should we add some throttling here?
				if (dataSetSearchJobs.get(subSystem).getState() == Job.RUNNING) {
					dataSetSearchJobs.get(subSystem).cancel();
				}
				
				dataSetSearchJobs.get(subSystem).setFilter(argumentValue);
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
		} else if (argumentName.equals("member")) {

			ISubSystem subSystem = findMvsSubsystem(arguments.get("host"));
			
			if (subSystem != null) {
				pdsMemberSearchFilters.put(subSystem, argumentValue);

				if (!pdsMemberSearchJobs.containsKey(subSystem)) {
					pdsMemberSearchJobs.put(subSystem, new QueryPDSMemberJob(subSystem));
				}
				
				//TODO should we add some throttling here?
				if (pdsMemberSearchJobs.get(subSystem).getState() == Job.RUNNING) {
					pdsMemberSearchJobs.get(subSystem).cancel();
				}
				
				pdsMemberSearchJobs.get(subSystem).setDataSetName(arguments.get("pds"));
				if (argumentValue.endsWith("*")) {
					pdsMemberSearchJobs.get(subSystem).setDataSetMemberFilter(argumentValue);
				} else {
					pdsMemberSearchJobs.get(subSystem).setDataSetMemberFilter(argumentValue + "*");
				}
				
				pdsMemberSearchJobs.get(subSystem).schedule();
				try {
					pdsMemberSearchJobs.get(subSystem).join();
				} catch (InterruptedException e) {
					throw new MCPException(e);
				}
				
				if (pdsMemberSearchJobs.get(subSystem).getResult().isOK()) {
					result.addAll(pdsMemberSearchJobs.get(subSystem).getResults());
				} else if (pdsMemberSearchJobs.get(subSystem).getResult().getSeverity() == IStatus.ERROR) {
					throw new MCPException(pdsMemberSearchJobs.get(subSystem).getResult());
				}
			}
		}
		return result;
	}

	@Override
	public String[] readResource(String url) {
		String[] split = url.split("/");
		if (split.length > 3) {
			String system = split[split.length - 3];
			String pds = split[split.length - 2];
			String member = split[split.length - 1];
			
			if (member.indexOf(".") > 0) {
				member = member.substring(0, member.indexOf("."));
			}
			
			ISubSystem subSystem = findMvsSubsystem(system);
			if (subSystem != null) {
				FetchPDSMemberContent job = new FetchPDSMemberContent(subSystem);
				job.setDataSetName(pds);
				job.setDataSetMemberFilter(member);
				job.schedule();
				try {
					job.join();
				} catch (InterruptedException e) {
					throw new MCPException(e);
				}
				
				IStatus status = job.getResult();
				if (status.isOK()) {
					return job.getConetnt().toArray(String[]::new);
				} else {
					throw new MCPException(status);
				}
			} else {
				throw new MCPException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Host not found: " + system));
			}
		}
		
		throw new MCPException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Malformed uri: " + url));
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
