package com.ibm.systemz.mcp.mvs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mcp.MCPException;
import org.eclipse.mcp.factory.IResourceAdapter;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.SystemStartHere;
import org.eclipse.rse.core.subsystems.ISubSystem;

import com.ibm.ftt.resources.zos.zosphysical.IZOSDataSetMember;
import com.ibm.ftt.rse.mvs.client.subsystems.IMVSFileSubSystem;
import com.ibm.systemz.mcp.mvs.job.FetchPDSMemberContent;
import com.ibm.systemz.mcp.mvs.job.QueryPDSMemberJob;

import io.modelcontextprotocol.spec.McpSchema.ResourceLink;

public class MvsResourceAdapter implements IResourceAdapter<IZOSDataSetMember> {

	final String template = "file://mvs/{host}/{pds}/{member}";

	@Override
	public String getTemplate() {
		return template;
	}

	@Override
	public String getUniqueTemplatePrefix() {
		return template.substring(0, template.indexOf("{"));
	}

	@Override
	public IZOSDataSetMember uriToEclipseObject(String uri) {
		String[] split = uri.split("/");
		if (split.length > 3) {
			String system = split[split.length - 3];
			String pds = split[split.length - 2];
			String member = split[split.length - 1];
			
			if (member.indexOf(".") > 0) {
				member = member.substring(0, member.indexOf("."));
			}
			
			ISubSystem subSystem = findMvsSubsystem(system);
			if (subSystem != null) {
				QueryPDSMemberJob job = new QueryPDSMemberJob(subSystem);
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
					return job.getMembers().getFirst();
				} else {
					throw new MCPException(status);
				}
			} else {
				throw new MCPException("Host not found: " + system);
			}
		}
		
		throw new MCPException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Malformed uri: " + uri));

	}

	@Override
	public Object eclipseObjectToJsonObject(IZOSDataSetMember member) {
		return null;
	}

	@Override
	public ResourceLink eclipseObjectToResourceLink(IZOSDataSetMember member) {
		return null;
	}

	@Override
	public String eclipseObjectToURI(IZOSDataSetMember member) {
		//TODO
		String connectionName = "???";
		return getUniqueTemplatePrefix() + connectionName + "/" + member.getDataset().getName() + member.getName();
	}

	@Override
	public String eclipseObjectToResourceContent(IZOSDataSetMember member) {	
		FetchPDSMemberContent fetchJob = new FetchPDSMemberContent(member);
		try {
			fetchJob.schedule();
			fetchJob.join();
		} catch (InterruptedException e) {
			throw new MCPException(e);
		}
		
		if (fetchJob.getResult().isOK()) {
			return fetchJob.getContent();
		} else {
			throw new MCPException(fetchJob.getResult());
		}
	}
	
	public static  ISubSystem findMvsSubsystem(String systemName) {
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
