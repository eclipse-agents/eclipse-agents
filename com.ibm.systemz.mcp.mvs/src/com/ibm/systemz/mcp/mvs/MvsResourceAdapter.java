package com.ibm.systemz.mcp.mvs;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mcp.IResourceAdapter;
import org.eclipse.mcp.MCPException;
import org.eclipse.mcp.Schema.DEPTH;
import org.eclipse.mcp.Schema.File;
import org.eclipse.mcp.Schema.Files;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.SystemStartHere;
import org.eclipse.rse.core.subsystems.ISubSystem;

import com.ibm.ftt.resources.core.physical.IPhysicalContainer;
import com.ibm.ftt.resources.core.physical.IPhysicalFile;
import com.ibm.ftt.resources.core.physical.IPhysicalResource;
import com.ibm.ftt.resources.zos.zosphysical.IZOSCatalog;
import com.ibm.ftt.resources.zos.zosphysical.IZOSDataSet;
import com.ibm.ftt.resources.zos.zosphysical.IZOSDataSetMember;
import com.ibm.ftt.resources.zos.zosphysical.IZOSDataSetMemberGeneration;
import com.ibm.ftt.resources.zos.zosphysical.IZOSGenerationDataGroup;
import com.ibm.ftt.resources.zos.zosphysical.IZOSPartitionedDataSet;
import com.ibm.ftt.resources.zos.zosphysical.IZOSSequentialDataSet;
import com.ibm.ftt.resources.zos.zosphysical.IZOSVsamDataSet;
import com.ibm.ftt.rse.mvs.client.subsystems.IMVSFileSubSystem;
import com.ibm.systemz.mcp.mvs.job.FetchPDSMemberContent;
import com.ibm.systemz.mcp.mvs.job.QueryDataSetsJob;
import com.ibm.systemz.mcp.mvs.job.QueryPDSMemberJob;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.ResourceLink;
import io.modelcontextprotocol.util.DefaultMcpUriTemplateManager;

public class MvsResourceAdapter implements IResourceAdapter<IPhysicalResource, File> {

	final String[] templates = new String[] {
			"file://mvs/{host}",
			"file://mvs/{host}/{pds}",
			"file://mvs/{host}/{pds}/{member}"
	};
	
	
	String host, pds, member;
	IPhysicalResource resource = null;
	IPhysicalResource parent = null;
	
	public MvsResourceAdapter() {}

	public MvsResourceAdapter(IPhysicalResource resource) {
		this(null, resource);
	}
	
	public MvsResourceAdapter(ISubSystem subSystem, IPhysicalResource resource) {
		this.resource = resource;
		
		if (subSystem != null) {
			this.host = subSystem.getHost().getName();
		}

		if (resource instanceof IZOSDataSetMember) {
			this.member = resource.getName();
			this.pds = resource.getParent().getName();
		} else if (resource instanceof IZOSPartitionedDataSet) {
			this.pds = resource.getName();
		}
	}
	
	public MvsResourceAdapter(String uri) {
		for (String template: templates) {
			DefaultMcpUriTemplateManager tm = new DefaultMcpUriTemplateManager(template);
			if (tm.matches(uri)) {
				Map<String, String> variables = tm.extractVariableValues(uri);
				this.host = variables.get("host");
				this.pds = variables.get("pds");
				this.member = variables.get("member");
				break;
			}
		}
	}

	@Override
	public String[] getTemplates() {
		return templates;
	}
	
	@Override
	public MvsResourceAdapter fromUri(String uri) {
		return new MvsResourceAdapter(uri);
	}

	@Override
	public MvsResourceAdapter fromModel(IPhysicalResource resource) {
		return new MvsResourceAdapter(resource);
	}

	@Override
	public boolean supportsChildren() {
		return lazyLoadResource() instanceof IPhysicalContainer;
	}

	@Override
	public Files getChildren(DEPTH depth) {
		List<File> files = new ArrayList<File>();
		IPhysicalResource resource = lazyLoadResource();
		if (resource != null) {
			if (resource instanceof IZOSPartitionedDataSet) {
				ISubSystem subSystem = findMvsSubsystem(host);
				if (subSystem != null) {
					List<IZOSDataSetMember> members = findPDSMember(subSystem, pds, "*");
					for (IZOSDataSetMember member: members) {
						files.add(new MvsResourceAdapter(subSystem, member).toJson());
					}
				}
			}
		}
		return new Files(files.toArray(File[]::new), DEPTH.CHILDREN);
	}

	@Override
	public IPhysicalResource getModel() {
		return resource;
	}

	@Override
	public File toJson() {
		return new File(resource.getName(), resource instanceof IPhysicalContainer, toResourceLink());
	}

	@Override
	public ResourceLink toResourceLink() {
		McpSchema.ResourceLink.Builder builder =  McpSchema.ResourceLink.builder()
				.uri(toUri())
				.name(resource.getName());
				
		if (resource instanceof IPhysicalFile) {
			builder.mimeType("text/plain");
		}
		
		resource.getResourceType();
		
		if (resource instanceof	IZOSGenerationDataGroup) {
			builder.description("Generation Data Group");
		} else if (resource instanceof IZOSPartitionedDataSet) {
			builder.description("Partitioned Data Set");
		} else if (resource instanceof IZOSCatalog) {
			builder.description("Catalog");
		} else if (resource instanceof IZOSDataSetMember) {
			builder.description("Data Set Member");
		} else if (resource instanceof IZOSDataSetMemberGeneration) {
			builder.description("Data Set Member Generation");
		} else if (resource instanceof IZOSSequentialDataSet) {
			builder.description("Sequential Data Set");
		} else if (resource instanceof IZOSVsamDataSet) {
			builder.description("VSAM Data Set");
		}
		
		//TODO
//		builder.size(info.getLength());

		return builder.build();
	}

	@Override
	public String toUri() {
		if (host != null) {
			if (pds != null) {
				if (member != null) {
					return "file://mvs/" +
							URLEncoder.encode(host, StandardCharsets.UTF_8) + "/" +
							URLEncoder.encode(pds, StandardCharsets.UTF_8) + "/" +
									URLEncoder.encode(member, StandardCharsets.UTF_8);
				} else {
					return "file://mvs/" +
							URLEncoder.encode(host, StandardCharsets.UTF_8) + "/" +
							URLEncoder.encode(pds, StandardCharsets.UTF_8);
				}
			} else {
				return "file://mvs/" +
						URLEncoder.encode(host, StandardCharsets.UTF_8);
			}
		}
		throw new MCPException("host not found");
	}

	@Override
	public String toContent() {
		IPhysicalResource resource = lazyLoadResource();
		
		if (resource instanceof IZOSDataSetMember) {
			FetchPDSMemberContent fetchJob = new FetchPDSMemberContent((IZOSDataSetMember)resource);
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
		
		return null;
	}


	public IPhysicalResource lazyLoadResource() {
		if (resource == null) {
			if (host != null) {
				ISubSystem subSystem = findMvsSubsystem(host);
				
				if (pds != null && member != null) {
					resource = findPDSMember(subSystem, pds, member).getFirst();
				} else if (pds != null) {
					resource = findPDS(subSystem, pds).getFirst();
				}
			}
		}
		
		return resource;
	}
	
	public static  ISubSystem findMvsSubsystem(String hostName) {
		for (IHost host : SystemStartHere.getConnections()) {
			if (host.getSystemType().getId().equals("com.ibm.etools.zos.system")) { //$NON-NLS-1$
				if (host.getHostName().equals(hostName)) {

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
	
	public static List<IZOSDataSet> findPDS(ISubSystem subSystem, String pdsName) {
		QueryDataSetsJob dataSetSearchJobs = new QueryDataSetsJob(subSystem, pdsName);
		dataSetSearchJobs.setFilter(pdsName);
		dataSetSearchJobs.schedule();
		try {
			dataSetSearchJobs.join();
		} catch (InterruptedException e) {
			throw new MCPException(e);
		}
		
		if (dataSetSearchJobs.getResult().isOK()) {
			return dataSetSearchJobs.getResults();
		} else {
			throw new MCPException(dataSetSearchJobs.getResult());
		}
	}
	
	public static List<IZOSDataSetMember> findPDSMember(ISubSystem subSystem, String pdsName, String memberName) {

		QueryPDSMemberJob job = new QueryPDSMemberJob(subSystem);
		job.setDataSetName(pdsName);
		job.setDataSetMemberFilter(memberName);
		job.schedule();
		try {
			job.join();
		} catch (InterruptedException e) {
			throw new MCPException(e);
		}
		
		IStatus status = job.getResult();
		if (status.isOK()) {
			return job.getMembers();
		} else {
			throw new MCPException(status);
		}
	}
	
}
