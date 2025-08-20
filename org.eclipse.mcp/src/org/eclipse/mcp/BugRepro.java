package org.eclipse.mcp;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures.SyncResourceSpecification;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.ClientCapabilities;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceRequest;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.Resource;
import io.modelcontextprotocol.spec.McpSchema.ResourceContents;
import io.modelcontextprotocol.spec.McpSchema.ResourceTemplate;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;
import jakarta.servlet.Servlet;

public class BugRepro {

	int port = 8583;
	String url =  "http://localhost:" + port + "/sse";
	
	public static void main(String... args) {
		new BugRepro();
	}
	
	public BugRepro () {
		super();
		startServer();
		runClient();
//		new Thread() {
//			public void run() {
//				try {
//					Thread.sleep(3000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}
//		}.start();
	}
	
	public void startServer() {
		
		
		HttpServletSseServerTransportProvider transportProvider =
			    new HttpServletSseServerTransportProvider(
			        new ObjectMapper(), "/", "/sse");
	
		
		ResourceTemplate template = new ResourceTemplate(
				"file://eclipse/{project}/{name}",
				"Eclipse Workspace File",
				"Content of an file in an Eclipse workspace",
				"plain/text", null);
//				annotation.mimeType(), templateAnnotation));
	
		Resource resource = Resource.builder()
			.uri(template.uriTemplate())
			.name(template.name())
			.description(template.description())
			.mimeType(template.mimeType())
			.annotations(template.annotations())
			.build();
		
		SyncResourceSpecification resourceSpec = new SyncResourceSpecification(resource, this::readResource);
		
		ServerCapabilities capabilities = ServerCapabilities.builder().resources(true, true) // Enable resource support
				.tools(true) // Enable tool support
				.prompts(false) // Enable prompt support
				.completions()
				.logging() // Enable logging support
				.build();
		
		
		// Create a server with custom configuration
		McpSyncServer syncServer = McpServer.sync(transportProvider)
			    .serverInfo("test", "1.0")
			    .capabilities(capabilities)
			    .resourceTemplates(new ResourceTemplate[] { template })
			    .build();
		
	
		
		syncServer.addResource(resourceSpec);
		
		syncServer.notifyResourcesListChanged();
	
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setName("test-Thread");
	
		Server jettyServer = new org.eclipse.jetty.server.Server(threadPool);
	
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
			System.out.println("Server initialized");
	
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public void runClient() {
		
		// Create a sync client with custom configuration
		HttpClientSseClientTransport transport = new HttpClientSseClientTransport(url);
		final McpSyncClient client = McpClient.sync(transport)
		    .requestTimeout(Duration.ofSeconds(10))
		    .capabilities(ClientCapabilities.builder().build())
		    .build();
		
		client.initialize();
		
		ReadResourceRequest req = new ReadResourceRequest("file://eclipse/project/name");
		ReadResourceResult result = client.readResource(req);
		for (ResourceContents rc: result.contents()) {
			System.out.println("Success");
			System.out.println(rc.toString());
		}
	
	}
	public ReadResourceResult readResource(McpSyncServerExchange exchange, ReadResourceRequest request) {
		List<ResourceContents> contents = new ArrayList<ResourceContents>();
		contents.add(new TextResourceContents(
				request.uri(),
				"text/plain",
				"Hello"));
		contents.add(new TextResourceContents(
				request.uri(),
				"text/plain",
				"Goodbye"));
		return new ReadResourceResult(contents);
	}
}
