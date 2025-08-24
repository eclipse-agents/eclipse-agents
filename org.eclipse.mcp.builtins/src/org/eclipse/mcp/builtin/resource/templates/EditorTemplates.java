package org.eclipse.mcp.builtin.resource.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.mcp.builtins.Activator;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedResourceTemplateFactory;
import org.eclipse.mcp.experimental.annotated.MCPAnnotatedResourceTemplateFactory.ResourceTemplate;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Contributes MCP Resource template "eclipse://editor/{name}" and completion assistance
 */
@ResourceTemplate (
		uriTemplate = "eclipse://editor/{name}",
		name = "Eclipse Editor",
		description = "Content of an Eclipse Text Editor")
public class EditorTemplates extends MCPAnnotatedResourceTemplateFactory {

	@Override
	public List<String> completionReq(String argumentName, String argumentValue, String uri, Map<String, String> arguments) {
		List<String> result = new ArrayList<String>();
		if (argumentName.equals("name")) {
			for (IWorkbenchWindow ww: PlatformUI.getWorkbench().getWorkbenchWindows()) {
				for (IWorkbenchPage page: ww.getPages()) {
					for (IEditorReference reference: page.getEditorReferences()) {
						result.add(reference.getName());
					}
				}
			}
		}
		return result;
	}

	@Override
	public String[] readResource(String uri) {
		String result = Activator.getDefault().getResourceContent(uri);
		if (result != null) {
			return new String[] { result };
		}
		return null;
	}
}
