package org.eclipse.mcp.builtin.resource;

import java.net.URLDecoder;
import java.util.Arrays;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.Annotations;
import io.modelcontextprotocol.spec.McpSchema.ResourceLink;
import io.modelcontextprotocol.spec.McpSchema.Role;

public class ConsoleAdapter implements IResourceAdapter<IConsole> {

	final String template = "eclipse://console/{name}";

	@Override
	public String getTemplate() {
		return template;
	}

	@Override
	public String getUniqueTemplatePrefix() {
		return template.substring(0, template.indexOf("{"));
	}

	@Override
	public IConsole uriToEclipseObject(String uri) {
		IConsole result = null;
		if (uri.startsWith(getUniqueTemplatePrefix())) {
			String name = uri.substring(getUniqueTemplatePrefix().length());
			IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
			for (IConsole console: manager.getConsoles()) {
				if (console.getName().equals(name)) {
					result = console;
				}
			}
			if (result == null) {
				name = URLDecoder.decode(name);
				for (IConsole console: manager.getConsoles()) {
					if (console.getName().equals(name)) {
						result = console;
					}
				}
			}
		}
		return result;
	}

	@Override
	public Object eclipseObjectToJsonObject(IConsole object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceLink eclipseObjectToResourceLink(IConsole console) {
		McpSchema.ResourceLink.Builder builder =  McpSchema.ResourceLink.builder()
				.uri(eclipseObjectToURI(console))
				.name(console.getName())
				.description("Content of an Eclipse IDE console")
				.mimeType("text/plain");
		
		addAnnotations(builder);
		
		return builder.build();

	}

	@Override
	public String eclipseObjectToURI(IConsole console) {
		return getUniqueTemplatePrefix() + console.getName();
	}

	@Override
	public String eclipseObjectToResourceContent(IConsole object) {
		
		
		//TODO
		

		return null;
	}

}
