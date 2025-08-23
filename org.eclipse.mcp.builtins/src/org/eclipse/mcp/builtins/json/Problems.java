package org.eclipse.mcp.builtins.json;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorReference;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Problems {
	@JsonProperty
	public Problem[] problems = null;
	
	public Problems(IResource resource) {
		try {
			List<Problem> children = new ArrayList<Problem>();
			for (IMarker marker: resource.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)) {
	    		children.add(new Problem(marker));
			}	
			problems = children.toArray(Problem[]::new);

		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		if (problems == null) {
			problems = new Problem[0];
		}
	}

	public Problems(IEditorReference resource) {
		// TODO Auto-generated constructor stub
	}
}
