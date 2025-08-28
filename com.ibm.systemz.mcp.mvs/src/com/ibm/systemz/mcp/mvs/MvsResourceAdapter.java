package com.ibm.systemz.mcp.mvs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mcp.MCPException;
import org.eclipse.mcp.builtin.resource.RelativeFileAdapter;
import org.eclipse.mcp.builtins.json.Resource;
import org.eclipse.mcp.factory.IResourceAdapter;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.SystemStartHere;
import org.eclipse.rse.core.subsystems.ISubSystem;

import com.ibm.ftt.resources.zos.zosphysical.IZOSDataSetMember;
import com.ibm.ftt.rse.mvs.client.subsystems.IMVSFileSubSystem;
import com.ibm.systemz.mcp.mvs.job.FetchPDSMemberContent;
import com.ibm.systemz.mcp.mvs.job.QueryPDSMemberJob;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.ResourceLink;
import io.modelcontextprotocol.util.DefaultMcpUriTemplateManager;

public class MvsResourceAdapter implements IResourceAdapter<IZOSDataSetMember> {

	final String template = "file://mvs/{host}/{pds}/{member}";
	
	
	String host, pds, member;
	
	public MvsResourceAdapter() {}

	public MvsResourceAdapter(IZOSDataSetMember member) {
		this.dsMember = member;
	}
	
	public MvsResourceAdapter(String uri) {
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
	public String getTemplate() {
		return template;
	}
	
	@Override
	public IResourceAdapter<IResource> fromUri(String uri) {
		return new RelativeFileAdapter(uri);
	}

	@Override
	public IResourceAdapter<IResource> fromModel(IResource console) {
		return new RelativeFileAdapter(console);
	}

	@Override
	public boolean supportsChildren() {
		return resource instanceof IContainer;
	}

	@Override
	public IResourceAdapter<IResource>[] getChildren(int depth) {
		
		List<RelativeFileAdapter> children = new ArrayList<RelativeFileAdapter>();
		depth = Math.max(0, Math.min(2, depth));

		if (resource instanceof IContainer) {
			try {
				for (IResource child: ((IContainer)resource).members()) {
					child.accept(new IResourceVisitor() {
						@Override
						public boolean visit(IResource child) throws CoreException {
							if (child != resource) {
								children.add(new RelativeFileAdapter(child));
							}
							return true;
						}
					}, depth, false);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		
		return children.toArray(RelativeFileAdapter[]::new);
	}

	@Override
	public IResource getModel() {
		return resource;
	}

	@Override
	public Object toJson() {
		return new Resource(resource);
	}

	@Override
	public ResourceLink toResourceLink() {
		McpSchema.ResourceLink.Builder builder =  McpSchema.ResourceLink.builder()
				.uri(toUri())
				.name(resource.getName());
				
		if (resource instanceof IFile) {
			builder.description("Eclipse workspace file");
			builder.mimeType("text/plain");

			try {
				IFileStore store = EFS.getStore(resource.getLocationURI());
				IFileInfo info = store.fetchInfo();
				builder.size(info.getLength());
				
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} else if (resource instanceof IProject) {
			builder.description("Eclipse workspace project");
		} else if (resource instanceof IWorkspaceRoot) {
			builder.description("Eclipse workspace root");
		} else if (resource instanceof IFolder) {
			builder.description("Eclipse workspace folder");
		}

		return builder.build();
		
	}

	@Override
	public String toUri() {
		return "file://workspace/" + URLEncoder.encode( resource.getFullPath().toPortableString().substring(1));
	}

	@Override
	public String toContent() {
		
		String content = null;
		if (resource instanceof IFile) {
			try {
				InputStreamReader reader = new InputStreamReader(((IFile)resource).getContents());
				BufferedReader breader = new BufferedReader(reader);
				content = breader.lines().collect(Collectors.joining("\n")); //$NON-NLS-1$
				breader.close();
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
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
