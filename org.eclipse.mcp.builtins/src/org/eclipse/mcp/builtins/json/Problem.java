package org.eclipse.mcp.builtins.json;

import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mcp.MCPException;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.modelcontextprotocol.spec.McpSchema;

/**
 * 
 * @param name
 * @param id
 * @param type
 */
@JsonClassDescription("An Eclipse IDE compilation or configuration issue")
public class Problem {

	enum SEVERITY { ERROR, INFO, WARNING };
	enum PRIORITY { HIGH, LOW, NORMAL };
	
	@JsonProperty(required = false)
	String type;
	
	@JsonProperty(required = false)
	@JsonPropertyDescription("An integer value indicating where a text marker starts. This attribute is zero-relative and inclusive.")
	int charStart;
	
	@JsonProperty(required = false)
	@JsonPropertyDescription("An integer value indicating where a text marker ends. This attribute is zero-relative and exclusive.")
	int charEnd;
	
	@JsonProperty(required = false)
	@JsonPropertyDescription("An integer value indicating the line number for a text marker. This attribute is 1-relative")
	int lineNumber;
	
	@JsonProperty(required = false)
	@JsonPropertyDescription("A boolean value indicating whether the marker")	
	boolean done;
	
	@JsonProperty(required = false)
	@JsonPropertyDescription("The location is a human-readable (localized) string which can be used to distinguish between markers on a resource")	
	String location = "";
	
	@JsonProperty
	@JsonPropertyDescription("The file being edited")
	McpSchema.ResourceLink resource_link;
	
	@JsonProperty
	long id;
	
	@JsonProperty
	long creationTime;
	
	SEVERITY severity;
	
	PRIORITY priority;

	
	public Problem(IMarker marker) {
		super();
		
		try {

			id = marker.getId();
			creationTime = marker.getCreationTime();
			
			Map<?, ?> map = marker.getAttributes();
			
			if (marker.isSubtypeOf(IMarker.BOOKMARK)) {
				type = "Bookmark";
			} else if (marker.isSubtypeOf(IMarker.PROBLEM)) {
				type = "Problem";
			} else if (marker.isSubtypeOf(IMarker.TASK)) {
				type = "Task";
			} else if (marker.isSubtypeOf(IMarker.TEXT)) {
				type = "Text";
			}
			
			Object obj = marker.getAttribute(IMarker.PRIORITY);
			if (obj != null) {
				if (obj.equals(IMarker.PRIORITY_HIGH)) {
					priority = PRIORITY.HIGH;
				} else if (obj.equals(IMarker.PRIORITY_NORMAL)) {
					priority = PRIORITY.NORMAL;
				} else if (obj.equals(IMarker.PRIORITY_LOW)) {
					priority = PRIORITY.LOW;
				}
			}

			obj = marker.getAttribute(IMarker.SEVERITY);
			if (obj != null) {
				if (obj.equals(IMarker.SEVERITY_ERROR)) {
					severity = SEVERITY.ERROR;
				} else if (obj.equals(IMarker.SEVERITY_INFO)) {
					severity = SEVERITY.INFO;
				} else if (obj.equals(IMarker.SEVERITY_WARNING)) {
					severity = SEVERITY.WARNING;
				}
			}
	
			if (map.containsKey(IMarker.CHAR_START) && 
					map.get(IMarker.CHAR_START) instanceof Integer) {
				charStart = (int)map.get(IMarker.CHAR_START);
			}
	
			if (map.containsKey(IMarker.CHAR_END) && 
					map.get(IMarker.CHAR_END) instanceof Integer) {
				charEnd = (int)map.get(IMarker.CHAR_END);
			}
			
			if (map.containsKey(IMarker.LINE_NUMBER) && 
					map.get(IMarker.LINE_NUMBER) instanceof Integer) {
				lineNumber = (int)map.get(IMarker.LINE_NUMBER);
			}
			
			if (map.containsKey(IMarker.DONE) && 
					map.get(IMarker.DONE) instanceof String) {
				done = "true".equals(map.get(IMarker.DONE));
			}
			
			if (map.containsKey(IMarker.LOCATION) && 
					map.get(IMarker.LOCATION) instanceof String) {
				location = (String)map.get(IMarker.LOCATION);
			}
						
			if (marker.getResource() instanceof IFile) {
				resource_link = Util.fileToResourceLink((IFile)marker.getResource());
			} else if (marker.getResource() instanceof IContainer) {
				resource_link = Util.containerToResourceLink((IContainer)marker.getResource());
			} 
			
		} catch (CoreException e) {
			throw new MCPException(e);
		}

	}
}