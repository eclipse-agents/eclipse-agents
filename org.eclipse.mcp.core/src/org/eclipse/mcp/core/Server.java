package org.eclipse.mcp.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.systemz.db2.ide.ConnectionEnvironment;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.LoggingLevel;
import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;
import jakarta.servlet.Servlet;

public class Server implements org.eclipse.ui.IStartup {

	private boolean copyLogsToSysError = true; // Boolean.getBoolean("com.ibm.systemz.db2.mcp.copyLogsToSysError");
												// //$NON-NLS-1$

	McpSyncServer syncServer;
	String url;
	Connection connection;

	
	public Server() {

		int port = 45450;
		
		try {
			
			this.url = "http://localhost:" + port + "/sse";

			HttpServletSseServerTransportProvider transportProvider =
				    new HttpServletSseServerTransportProvider(
				        new ObjectMapper(), "/", "/sse");
			
			ServerCapabilities capabilities = ServerCapabilities.builder().resources(false, false) // Enable resource support
					.tools(true) // Enable tool support
					.prompts(false) // Enable prompt support
					.logging() // Enable logging support
					.build();
			
			// Create a server with custom configuration
			this.syncServer = McpServer.sync(transportProvider)
				    .serverInfo("IBM Developer for z", "0.0.1")
				    .capabilities(capabilities)
				    .build();
			
			log(LoggingLevel.INFO, this, url);

			// Resources
//			new Schema(this, "ADMF001");
//			new Schema(this, "SYSIBM");
			

//			syncServer.notifyToolsListChanged();

			QueuedThreadPool threadPool = new QueuedThreadPool();
			threadPool.setName("mcp-server");

			org.eclipse.jetty.server.Server jettyServer = new org.eclipse.jetty.server.Server(threadPool);

			ServerConnector connector = new ServerConnector(jettyServer);
			connector.setPort(45450);
			jettyServer.addConnector(connector);

			ServletContextHandler context = new ServletContextHandler();
			context.setContextPath("/");
			context.addServlet(new ServletHolder((Servlet)transportProvider), "/*");
			jettyServer.setHandler(context);
			jettyServer.start();
			
//			syncServer.notifyToolsListChanged();

			// Send logging notifications
			log(LoggingLevel.INFO, this, "Server initialized");

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	public McpSyncServer getSyncServer() {
		return syncServer;
	}


	public CallToolResult runQuery(String connectionId, String sqlContent) {
		
		Object response = ConnectionEnvironment.executeStatement2(connectionId, sqlContent);

		List<Content> result = new ArrayList<Content>();
//		boolean isError = false;
//		if (response instanceof Throwable) {
//			result.add(new TextContent(((Throwable)response).getLocalizedMessage()));
//			isError = true;
//		} else if (response instanceof QueryModel) {
//			//TODO needs hardening
//			Execution execution = ((QueryModel)response).executions.get(0);
//			ExecutionStatus executionStatus = execution.executionStatus;
//			if ("-1".equals(executionStatus.returnCode)) {
//				JsonObject o = new JsonObject();
//				o.addProperty("returnCode", executionStatus.returnCode);
//				o.addProperty("updateCount", executionStatus.updateCount);
//				o.addProperty("sqlCode", executionStatus.sqlCode);
//				o.addProperty("sqlState", executionStatus.sqlState);
//				o.addProperty("messageText", executionStatus.messageText);
//				o.addProperty("elapsedTime", executionStatus.elapsedTime);
//				result.add(new TextContent(o.toString()));
//				isError = true;
//			} else {
//				ResultSet resultSet = execution.resultSets[0];
//				for (String[] row: resultSet.getRows()) {
//					JsonObject o = new JsonObject();
//					for (int i = 0; i < resultSet.colCount; i++) {
//						o.addProperty(resultSet.columnNames[i], row[i]);
//					}
//					result.add(new TextContent(o.toString()));
//				}
//				return new CallToolResult(result, false);
//			}
//		} else {
//			//TODO
//			result.add(new TextContent("Unexpected Response"));
//		}
		return new CallToolResult(result, false);
	}
	
	public String getUrl() {
		return url;
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

	public void log(Object source, String message, SQLException ex) {
		log(LoggingLevel.ERROR, source, "SQLException information");
		while (ex != null) {
			log(LoggingLevel.ERROR, source, "Error msg: " + ex.getMessage());
			log(LoggingLevel.ERROR, source, "SQLSTATE: " + ex.getSQLState());
			log(LoggingLevel.ERROR, source, "Error code: " + ex.getErrorCode());
			log(LoggingLevel.ERROR, source, ex.getLocalizedMessage());
			ex = ex.getNextException(); // For drivers that support chained exceptions
		}
	}

	@Override
	public void earlyStartup() {
	}

}
