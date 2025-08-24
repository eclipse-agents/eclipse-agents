package org.eclipse.mcp.builtins.json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.texteditor.ITextEditor;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tasks {
	@JsonProperty(value = "tasks")
	public Marker[] tasks = null;
	
	public Tasks(IResource resource) {
		try {
			List<Marker> children = new ArrayList<Marker>();
			for (IMarker marker: resource.findMarkers(IMarker.TASK, true, IResource.DEPTH_INFINITE)) {
	    		children.add(new Marker(marker));
			}	
			tasks = children.toArray(Marker[]::new);

		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		if (tasks == null) {
			tasks = new Marker[0];
		}
	}

	public Tasks(ITextEditor editor) {
		List<Marker> children = new ArrayList<Marker>();
		IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		IAnnotationModel model = editor.getDocumentProvider().getAnnotationModel(editor.getEditorInput());
		Iterator<Annotation> iterator = model.getAnnotationIterator();
		while (iterator.hasNext()) {
			Annotation annotation = iterator.next();
			Marker child = new Marker(annotation, model.getPosition(annotation), document, editor);
			if (Marker.TYPE.Task.label().equals(child.type)) {
				children.add(child);
			}
		}
		
		tasks = children.toArray(Marker[]::new);
		
	}
}
