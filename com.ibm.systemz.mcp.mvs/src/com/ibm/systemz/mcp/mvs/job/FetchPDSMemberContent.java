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
import org.eclipse.rse.core.subsystems.ISubSystem;

import com.ibm.ftt.resource.utils.PBResourceUtils;
import com.ibm.ftt.resources.zos.zosphysical.IZOSDataSetMember;



/**
 * See: com.ibm.ftt.rse.mvs.client.ui.dialogs.QueryDataSetsJob
 */
public class FetchPDSMemberContent extends QueryPDSMemberJob {

	private List<String> content;

	public FetchPDSMemberContent(ISubSystem subSystem){
		super(subSystem);

		setName("Model Context Protocol (MVS)");
		
		this.subSystem = subSystem;
		this.content = new ArrayList<String>();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		content.clear();

		IStatus queryPDSMemberStatus = super.run(monitor);
		
		if (!queryPDSMemberStatus.isOK()) {
			return queryPDSMemberStatus;
		}
	
		monitor.beginTask("Downloading " + dataSetName + "(" + dataSetMemberFilter + ")", IProgressMonitor.UNKNOWN);
		
		List<IZOSDataSetMember> members = getMembers();
		
		if (members != null && !members.isEmpty()) {
			if (members.size() > 1) {
				//TODO
			}
			IZOSDataSetMember member = members.get(0);
			
			//TODO determine if content needs to be refreshed
			IFile file = PBResourceUtils.copyFileToLocal(member, monitor);
			
			try (InputStreamReader reader = new InputStreamReader(
					file.getContents(), file.getCharset())) {
			       
				BufferedReader breader = new BufferedReader(reader);
				String read = breader.lines().collect(Collectors.joining("\n")); //$NON-NLS-1$
				if (read != null) {
					content.add(read);
				}
			}catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

//			member.getContents();
//			member.getFullPath();
			
			

//			boolean download = true;
//			if (member instanceof ZOSResource) {
//				IMVSResource memberMVS = ((ZOSResource) member).getMvsResource();
//				if (memberMVS instanceof AbstractMVSResource) {
//					iCopybook = memberMVS.getLocalResource();
//		
//					// if it is not in the cache anymore we have to
//					// download the file
//					// and - if the user clears the cache, we will come here
//					// and get a fresh set of copybooks
//					if (iCopybook != null && !iCopybook.exists()) {
//						download = true;
//					} else if (memberMVS.getModifiedDate() == null && ((AbstractMVSResource) memberMVS).isDownloaded()) {
//						// Don't download the file, it was already downloaded and there were no
//						// timestamps
//						// on the host, so it should not be downloaded again.
//						download = false;
//					} else if (((AbstractMVSResource) memberMVS).isDownloaded()
//							&& !((AbstractMVSResource) memberMVS).isDownloadNeeded()) {
//						// The copybook has stats, and neither the content nor the mapping has changed,
//						// so there is no need to download. If download was true, getFile() would
//						// eventually be called and trigger another call to the host to get file
//						// properties, which has already been done in the searchLibraries() method
//						// above.
//						download = false;
//					}
//				}
//			}
			
			
			
			
				
		} else {
			
		}
		
		
			/////////////
			///
//		catalog.setStale(true);
//		IAdaptable member = ((ZOSCatalog) catalog).findMember(memberName, monitor); // need to use a monitor here!
//
//		if (monitor.isCanceled()) {
//			return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Cancelled");
//		}
//		if (member instanceof IZOSDataSet) {
//			
//		}
//
		return Status.OK_STATUS;
	}
	
	public List<String> getConetnt() {
		return content;
	}

}
