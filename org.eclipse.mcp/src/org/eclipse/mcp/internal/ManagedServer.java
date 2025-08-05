package org.eclipse.mcp.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import javax.swing.JOptionPane;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.mcp.MCPException;
import org.eclipse.mcp.internal.preferences.IPreferencedServer;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.LoggingLevel;
import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import jakarta.servlet.Servlet;

public class ManagedServer {

	ExtensionManager.Server extension;
	private boolean copyLogsToSysError = true; // Boolean.getBoolean("com.ibm.systemz.db2.mcp.copyLogsToSysError");

	McpSyncServer syncServer;
	QueuedThreadPool threadPool;
	String url;
	
	public ManagedServer(ExtensionManager.Server extension) {
		this.extension = extension;
	}
	
	public void start() {
	
		this.url = "http://localhost:" + extension.getDefaultPort() + "/sse";

		
		HttpServletSseServerTransportProvider transportProvider =
			    new HttpServletSseServerTransportProvider(
			        new ObjectMapper(), "/", "/sse");
		
		ServerCapabilities capabilities = ServerCapabilities.builder().resources(false, false) // Enable resource support
				.tools(extension.getTools().length > 0) // Enable tool support
				.prompts(false) // Enable prompt support
				.logging() // Enable logging support
				.build();
	
		// Create a server with custom configuration
		this.syncServer = McpServer.sync(transportProvider)
			    .serverInfo(extension.getName(), extension.getVersion())
			    .capabilities(capabilities)
//			    .resourceTemplates(builtins.templates.templates)
			    .build();
		
		
		log(LoggingLevel.INFO, this, url);
	
		for (ExtensionManager.Tool toolExtension: extension.getTools()) {
			Tool tool = new Tool(toolExtension.getName(), toolExtension.getDescription(), toolExtension.getSchema());				
			
			SyncToolSpecification spec = new SyncToolSpecification(tool, new BiFunction<McpSyncServerExchange, Map<String, Object>, McpSchema.CallToolResult>() {
				@Override
				public CallToolResult apply(McpSyncServerExchange t, Map<String, Object> u) {
					CallToolResult result = null;
					List<Content> content = new ArrayList<Content>();
					
					try {

						ElementProperties elementProperties = new ElementProperties(extension.getId(), toolExtension.getId(), toolExtension.getName(), Images.IMG_TOOL, toolExtension.getPropertyEditorIds());
						
						String[] rawText = toolExtension.getImplementation().apply(u, elementProperties);
						for (String s: rawText) {
							content.add(new TextContent(s));
						}
						result = new CallToolResult(content, false);
					} catch (Exception e) {
						content.add(new TextContent(e.getLocalizedMessage()));
						e.printStackTrace();
						result = new CallToolResult(content, true);
					}
					return result;
				}
			});
			syncServer.addTool(spec);
		}
		
		for (ExtensionManager.ResourceController resourceFactory: extension.getResourceControllers()) {	
			ResourceManager resourceManager = new ResourceManager(this, resourceFactory.getImplementation());
			resourceFactory.getImplementation().initialize(resourceManager);
		}

		syncServer.notifyToolsListChanged();
		syncServer.notifyResourcesListChanged();
	
		threadPool = new QueuedThreadPool();
		threadPool.setName(extension.getName() + "-Thread");

		org.eclipse.jetty.server.Server jettyServer = new org.eclipse.jetty.server.Server(threadPool);
	
		ServerConnector connector = new ServerConnector(jettyServer);
		connector.setPort(Integer.parseInt(extension.getDefaultPort()));
		jettyServer.addConnector(connector);

		try {
			ServletContextHandler context = new ServletContextHandler();
			context.setContextPath("/");
			context.addServlet(new ServletHolder((Servlet)transportProvider), "/*");
			jettyServer.setHandler(context);
			jettyServer.start();
			
			syncServer.notifyToolsListChanged();
	
			// Send logging notifications
			log(LoggingLevel.INFO, this, "Server initialized");

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	McpSyncServer getSyncServer() {
		return syncServer;
	}
	
	public void stop() {
		
	}

	public void log(McpSchema.LoggingLevel level, Object source, String message) {
	
		if (copyLogsToSysError) {
			System.err.println(message);
		}
	
		Class<?> sourceClass;
		if (!(source instanceof Class)) {
			sourceClass = source.getClass();
		} else {
			sourceClass = (Class<?>) source;
		}
	
		syncServer.loggingNotification(LoggingMessageNotification.builder().level(level)
			.logger(sourceClass.getCanonicalName()).data(message).build());
	}
	
	public void log(Object source, MCPException ex) {
		log(LoggingLevel.ERROR, source, "MCP Implementation Exception");
		while (ex != null) {
			log(LoggingLevel.ERROR, source, "Error msg: " + ex.getMessage());
			log(LoggingLevel.ERROR, source, ex.getLocalizedMessage());
//			ex = ex.getNextException(); // For drivers that support chained exceptions
		}
	}
}
