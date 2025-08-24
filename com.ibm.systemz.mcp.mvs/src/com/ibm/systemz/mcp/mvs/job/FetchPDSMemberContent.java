package com.ibm.systemz.mcp.mvs.job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.rse.core.subsystems.ISubSystem;

import com.ibm.ftt.resource.utils.PBResourceUtils;
import com.ibm.ftt.resources.zos.zosphysical.IZOSDataSetMember;



/**
 * See: com.ibm.ftt.rse.mvs.client.ui.dialogs.QueryDataSetsJob
 */
public class FetchPDSMemberContent extends Job {

	private String content;
	
	private IZOSDataSetMember member;

	public FetchPDSMemberContent(IZOSDataSetMember member){
		super("Model Context Protocol (MVS)");
		
		this.member = member;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		content = null;
	
		monitor.beginTask("Downloading " + member.getDataset().getName() + "(" + member.getName() + ")", IProgressMonitor.UNKNOWN);
		
		IFile file = PBResourceUtils.copyFileToLocal(member, monitor);
		
		try (InputStreamReader reader = new InputStreamReader(
				file.getContents(), file.getCharset())) {
		       
			BufferedReader breader = new BufferedReader(reader);
			content = breader.lines().collect(Collectors.joining("\n")); //$NON-NLS-1$

		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return Status.OK_STATUS;
	}
	
	public String getContent() {
		return content;
	}

}
