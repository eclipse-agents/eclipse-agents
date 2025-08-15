package org.eclipse.mcp.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.mcp.MCPException;
import org.eclipse.mcp.factory.IFactory;
import org.eclipse.mcp.factory.IFactoryProvider;
import org.eclipse.mcp.factory.IResourceFactory;
import org.eclipse.mcp.factory.IResourceTemplateFactory;
import org.eclipse.mcp.factory.ToolFactory;
import org.eclipse.mcp.factory.ToolFactory.ToolVisibilityListener;
import org.eclipse.ui.PlatformUI;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures.SyncCompletionSpecification;
import io.modelcontextprotocol.server.McpServerFeatures.SyncResourceSpecification;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.LoggingLevel;
import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;
import jakarta.servlet.Servlet;

public class ManagedServer implements ToolVisibilityListener {

	String name, version;
	int port;
	
	// For dynamically adding/removing tools
	boolean running = false;
	Map<ToolFactory, McpSchema.Tool > toolMap = new HashMap<ToolFactory, McpSchema.Tool >();
	Map<ToolFactory, SyncToolSpecification> toolSpecMap = new HashMap<ToolFactory, SyncToolSpecification>();
	Map<ToolFactory, String> servedToolNamesMap = new HashMap<ToolFactory, String>();
	
	
	
	List<IResourceTemplateFactory> resourceTemplateFactories;
	List<ToolFactory> toolFactories;
	List<IResourceFactory> resourceFactories;
			
	private boolean copyLogsToSysError = true; // Boolean.getBoolean("com.ibm.systemz.db2.mcp.copyLogsToSysError");

	McpSyncServer syncServer;
	QueuedThreadPool threadPool;
	String url;
	
	org.eclipse.jetty.server.Server jettyServer = null;
	
	public ManagedServer(String name, String version, int port, IFactory[] factories) {
		this.name = name;
		this.version = version;
		this.port = port;
		
		resourceTemplateFactories = new ArrayList<IResourceTemplateFactory>();
		toolFactories = new ArrayList<ToolFactory>();
		resourceFactories = new ArrayList<IResourceFactory>();
		
		for (IFactory factory: factories) {
			if (factory instanceof IResourceTemplateFactory) {
				resourceTemplateFactories.add((IResourceTemplateFactory)factory);
			} else if (factory instanceof IResourceFactory) {
				resourceFactories.add((IResourceFactory)factory);
			} else if (factory instanceof ToolFactory) {
				toolFactories.add((ToolFactory)factory);
			} else if (factory instanceof IFactoryProvider) {
				resourceTemplateFactories.addAll(Arrays.asList(
						((IFactoryProvider)factory).createResourceTemplateFactories()));
				
				resourceFactories.addAll(Arrays.asList(
						((IFactoryProvider)factory).createResourceFactories()));
				
				toolFactories.addAll(Arrays.asList(
						((IFactoryProvider)factory).createToolFactories()));
			}
		}
		
		for (ToolFactory toolFactory: toolFactories) {
			toolFactory.addVisibilityListener(this);
		}
	}
	
	public void start() {
	
		this.url = "http://localhost:" + port + "/sse";

		HttpServletSseServerTransportProvider transportProvider =
			    new HttpServletSseServerTransportProvider(
			        new ObjectMapper(), "/", "/sse");
	
		List<McpSchema.ResourceTemplate> templates = new ArrayList<McpSchema.ResourceTemplate>();
		List<SyncCompletionSpecification> completions = new ArrayList<SyncCompletionSpecification>();
		List<SyncResourceSpecification> templateResourceSpecs = new ArrayList<SyncResourceSpecification>();

	
		for (IResourceTemplateFactory templateFactory: resourceTemplateFactories) {
			for (McpSchema.ResourceTemplate template: templateFactory.createResourceTemplates()) {
				templates.add(template);
				completions.add(templateFactory.createCompletionSpecification(template));
				templateResourceSpecs.add(templateFactory.getResourceTemplateSpecification(template));
			}
		}
		

		ServerCapabilities capabilities = ServerCapabilities.builder().resources(true, true) // Enable resource support
				.tools(true) // Enable tool support
				.prompts(false) // Enable prompt support
				.completions()
				.logging() // Enable logging support
				.build();
		
		
		// Create a server with custom configuration
		this.syncServer = McpServer.sync(transportProvider)
			    .serverInfo(name, version)
			    .capabilities(capabilities)
			    .resourceTemplates(templates.toArray(McpSchema.ResourceTemplate[]::new))
			    .completions(completions)
			    .build();
		
		log(LoggingLevel.INFO, this, url);
	
		running = true;
		for (ToolFactory toolFactory: toolFactories) {
			
			McpSchema.Tool tool = toolFactory.createTool();
			toolMap.put(toolFactory, tool);
			
			SyncToolSpecification spec = toolFactory.createSpec(tool);
			toolSpecMap.put(toolFactory, spec);
			
			if (toolFactory.isVisible()) {
				syncServer.addTool(spec);
				servedToolNamesMap.put(toolFactory, tool.name());
			}
		}
		
		for (IResourceFactory resourceFactory: resourceFactories) {
			resourceFactory.initialize(new ResourceManager(this, resourceFactory));
		}
		
		for (SyncResourceSpecification spec: templateResourceSpecs) {
			syncServer.addResource(spec);
		}

		syncServer.notifyResourcesListChanged();
	
		threadPool = new QueuedThreadPool();
		threadPool.setName(name + "-Thread");

		jettyServer = new org.eclipse.jetty.server.Server(threadPool);
	
		ServerConnector connector = new ServerConnector(jettyServer);
		connector.setPort(port);
		jettyServer.addConnector(connector);

		try {
			ServletContextHandler context = new ServletContextHandler();
			context.setContextPath("/");
			context.addServlet(new ServletHolder((Servlet)transportProvider), "/*");
			jettyServer.setHandler(context);
			jettyServer.start();
			jettyServer.setStopAtShutdown(true);
			
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
		running = false;
		for (ToolFactory toolFactory: toolSpecMap.keySet()) {
			toolFactory.removeVisibilityListener(this);
		}

		if (syncServer != null) {
			syncServer.closeGracefully();
		}
		
		if (jettyServer != null) {
			try {
				jettyServer.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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

	@Override
	public void visibilityChanged(ToolFactory toolFactory) {
		if (running) {
			
			McpSchema.Tool tool = toolMap.get(toolFactory);
			SyncToolSpecification toolSpec = toolSpecMap.get(toolFactory);
			
			if (tool != null && toolSpec != null) {
				// the tool factory was not activity disabled at server start time
				
				if (toolFactory.isVisible() &&
						!servedToolNamesMap.containsKey(toolFactory)) {
					
					// factory wishes to be visible but is not currently served
					syncServer.addTool(toolSpec);
					servedToolNamesMap.put(toolFactory, tool.name());

				} else if (!toolFactory.isVisible() &&
						servedToolNamesMap.containsKey(toolFactory)) {
					
					// factory wishes to be invisible but is currently served
					syncServer.removeTool(tool.name());
					servedToolNamesMap.remove(toolFactory);
				}
			}
		}
		
	}
}
