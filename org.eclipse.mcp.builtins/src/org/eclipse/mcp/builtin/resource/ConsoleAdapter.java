package org.eclipse.mcp.builtin.resource;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import org.eclipse.mcp.MCPException;
import org.eclipse.mcp.builtins.json.Console;
import org.eclipse.mcp.factory.IResourceAdapter;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.TextConsole;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.ResourceLink;
import io.modelcontextprotocol.util.DefaultMcpUriTemplateManager;

public class ConsoleAdapter implements IResourceAdapter<IConsole> {

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
			name = URLDecoder.decode(name);

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
	public String getTemplate() {
		return template;
	}
	
	@Override
	public IResourceAdapter<IConsole> fromUri(String uri) {
		return new ConsoleAdapter(uri);
	}

	@Override
	public IResourceAdapter<IConsole> fromModel(IConsole console) {
		return new ConsoleAdapter(console);
	}

	@Override
	public boolean supportsChildren() {
		return false;
	}

	@Override
	public IResourceAdapter<IConsole>[] getChildren(int depth) {
		return new ConsoleAdapter[0];
	}

	@Override
	public IConsole getModel() {
		return console;
	}

	@Override
	public Object toJson() {
		return new Console(console);
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
		return prefix + URLEncoder.encode(console.getName());
	}

	@Override
	public String toContent() {
		if (console instanceof TextConsole) {
			return ((TextConsole)console).getDocument().get();
		}
		return "...";
	}

}
