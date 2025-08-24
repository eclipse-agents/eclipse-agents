package org.eclipse.mcp.builtins.json;

import java.util.Map;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;
import org.eclipse.mcp.MCPException;
import org.eclipse.mcp.builtin.resource.EditorAdapter;
import org.eclipse.mcp.builtin.resource.RelativeFileAdapter;
import org.eclipse.ui.texteditor.ITextEditor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.modelcontextprotocol.spec.McpSchema;


public class Marker {

	enum SEVERITY { ERROR, INFO, WARNING };
	enum PRIORITY { HIGH, LOW, NORMAL };
	enum TYPE { Bookmark("Bookmark"), 
		Problem("Problem"), 
		Task("Task"), 
		Text("Text");
		
		String label;
		private TYPE(String label) {
			this.label = label;
		}
		
		public String label() {
			return label;
		}
	};
	
	@JsonProperty(required = false)
	String type;
	
	@JsonProperty(required = false)
	String message;
	
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
	@JsonPropertyDescription("The associated file or editor")
	McpSchema.ResourceLink resource_link;
	
	@JsonProperty
	long id;
	
	@JsonProperty
	long creationTime;
	
	SEVERITY severity;
	
	PRIORITY priority;

	
	public Marker(IMarker marker) {
		super();
		
		try {
			processMarker(marker);
			resource_link = new RelativeFileAdapter().eclipseObjectToResourceLink(marker.getResource());
		} catch (Exception e) {
			throw new MCPException(e);
		}
	}
	
	public Marker(Annotation annotation, Position position, IDocument document, ITextEditor editor) {
		super();
		
		try {
			type = annotation.getType();
		
			if (annotation instanceof SimpleMarkerAnnotation) {
				processMarker(((SimpleMarkerAnnotation)annotation).getMarker());
			} else if ("org.eclipse.ui.workbench.texteditor.error".equals(type)) {
				type = TYPE.Problem.label();
				severity = SEVERITY.ERROR;
			} else if ("org.eclipse.ui.workbench.texteditor.warning".equals(type)) {
				type = TYPE.Problem.label();
				severity = SEVERITY.WARNING;
			} else if ("org.eclipse.ui.workbench.texteditor.info".equals(type)) {
				type = TYPE.Problem.label();
				severity = SEVERITY.INFO;
			} else if ("org.eclipse.ui.workbench.texteditor.task".equals(type)) {
				type = TYPE.Task.label();
			} else if ("org.eclipse.ui.workbench.texteditor.bookmark".equals(type)) {
				type = TYPE.Bookmark.label();
			}

			message = annotation.getText();
			charStart = position.offset;
			charEnd = position.getOffset() + position.getLength();
			lineNumber = document.getLineOfOffset(position.getOffset());
			resource_link = new EditorAdapter().eclipseObjectToResourceLink(editor);
		} catch (Exception e) {
			throw new MCPException(e);
		}
	}
	
	public void processMarker(IMarker marker) throws Exception {
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

		if (map.containsKey(IMarker.MESSAGE)) {
			message = map.get(IMarker.MESSAGE).toString();
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
		
		if (map.containsKey(IMarker.DONE)) {
			done = "true".equals(map.get(IMarker.DONE).toString());
		}
		
		if (map.containsKey(IMarker.LOCATION) && 
				map.get(IMarker.LOCATION) instanceof String) {
			location = (String)map.get(IMarker.LOCATION);
		}
	}
}