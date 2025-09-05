package org.eclipse.mcp.platform.resource;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.mcp.MCPException;
import org.eclipse.mcp.platform.resource.ResourceSchema.Console;
import org.eclipse.mcp.platform.resource.ResourceSchema.Consoles;
import org.eclipse.mcp.resource.IResourceTemplate;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.TextConsole;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.ResourceLink;
import io.modelcontextprotocol.util.DefaultMcpUriTemplateManager;

/**
 * support for resource template: eclipse://console/{name}
 */
public class ConsoleAdapter implements IResourceTemplate<IConsole, Console> {

	final String template = "eclipse://console/{name}";
	final String prefix = template.substring(0, template.indexOf("{"));
	IConsole console = null;
	
	public ConsoleAdapter() {}
	
	public ConsoleAdapter(IConsole console) {
		this.console = console;
	}
	
	public ConsoleAdapter(String uri) {
		DefaultMcpUriTemplateManager tm = new DefaultMcpUriTemplateManager(template);
		if (tm.matches(uri)) {
			Map<String, String> variables = tm.extractVariableValues(uri);
			String name = variables.get("name");
			name = URLDecoder.decode(name, StandardCharsets.UTF_8);

			IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
			for (IConsole console: manager.getConsoles()) {
				if (console.getName().equals(name)) {
					this.console = console;
				}
			}
		}
		
		if (console == null) {
			throw new MCPException("uri not resolved: " + uri);
		}
	}

	@Override
	public String[] getTemplates() {
		return new String[] { template };
	}
	
	@Override
	public ConsoleAdapter fromUri(String uri) {
		return new ConsoleAdapter(uri);
	}

	@Override
	public ConsoleAdapter fromModel(IConsole console) {
		return new ConsoleAdapter(console);
	}

	@Override
	public IConsole getModel() {
		return console;
	}

	@Override
	public Console toJson() {
		return new Console(console.getName(), console.getType(), toUri());
	}

	@Override
	public ResourceLink toResourceLink() {
		return McpSchema.ResourceLink.builder()
				.uri(toUri())
				.name(console.getName())
				.description("Content of an Eclipse IDE console")
				.mimeType("text/plain")
				.build();
	}

	@Override
	public String toUri() {
		return prefix + URLEncoder.encode(console.getName(), StandardCharsets.UTF_8);
	}

	@Override
	public String toContent() {
		if (console instanceof TextConsole) {
			return ((TextConsole)console).getDocument().get();
		}
		return "...";
	}
	
	public static Consoles getConsoles() {
		List<Console> consoles = new ArrayList<Console>();
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		for (IConsole console : manager.getConsoles()) {
			ConsoleAdapter adapter = new ConsoleAdapter(console);
			consoles.add(adapter.toJson());
		}
		return new Consoles(consoles.toArray(Console[]::new));
	}

}
