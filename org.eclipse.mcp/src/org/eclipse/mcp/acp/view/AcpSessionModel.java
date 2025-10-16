/*******************************************************************************
 * Copyright (c) 2025 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.mcp.acp.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mcp.acp.AcpService;
import org.eclipse.mcp.acp.IAcpSessionListener;
import org.eclipse.mcp.acp.agent.IAgentService;
import org.eclipse.mcp.acp.protocol.AcpSchema.CancelNotification;
import org.eclipse.mcp.acp.protocol.AcpSchema.CreateTerminalRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.CreateTerminalResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.KillTerminalCommandRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.KillTerminalCommandResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.McpServer;
import org.eclipse.mcp.acp.protocol.AcpSchema.PlanEntry;
import org.eclipse.mcp.acp.protocol.AcpSchema.PlanEntryStatus;
import org.eclipse.mcp.acp.protocol.AcpSchema.PromptRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.PromptResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.ReadTextFileRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.ReadTextFileResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.ReleaseTerminalRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.ReleaseTerminalResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.RequestPermissionRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.RequestPermissionResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.SessionAgentMessageChunk;
import org.eclipse.mcp.acp.protocol.AcpSchema.SessionAgentThoughtChunk;
import org.eclipse.mcp.acp.protocol.AcpSchema.SessionAvailableCommandsUpdate;
import org.eclipse.mcp.acp.protocol.AcpSchema.SessionModeState;
import org.eclipse.mcp.acp.protocol.AcpSchema.SessionModeUpdate;
import org.eclipse.mcp.acp.protocol.AcpSchema.SessionNotification;
import org.eclipse.mcp.acp.protocol.AcpSchema.SessionPlan;
import org.eclipse.mcp.acp.protocol.AcpSchema.SessionToolCall;
import org.eclipse.mcp.acp.protocol.AcpSchema.SessionToolCallUpdate;
import org.eclipse.mcp.acp.protocol.AcpSchema.SessionUserMessageChunk;
import org.eclipse.mcp.acp.protocol.AcpSchema.SetSessionModeRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.SetSessionModeResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.TerminalOutputRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.TerminalOutputResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.TextBlock;
import org.eclipse.mcp.acp.protocol.AcpSchema.WaitForTerminalExitRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.WaitForTerminalExitResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.WriteTextFileRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.WriteTextFileResponse;

public class AcpSessionModel implements IAcpSessionListener {

	// Initialization
	IAgentService agent;
	String sessionId; 
	String cwd;
	McpServer[] mcpServers; 
	SessionModeState modes;
	
	// State
//	int promptId = 0;
	List<Object> session = new ArrayList<Object>();
	
	AcpView view;
	AcpBrowser browser;
	
	enum MessageType { session_prompt, user_message_chunk, agent_thought_chunk, agent_message_chunk, resource_link };

	
	public AcpSessionModel(IAgentService agent, String sessionId, String cwd, McpServer[] mcpServers, SessionModeState modes) {
		this.agent = agent;
		this.sessionId = sessionId;
		this.cwd = cwd;
		this.mcpServers = mcpServers;  
		this.modes = modes;
		
		AcpService.instance().addAcpListener(this);
	}
	
	@Override
	public String getSessionId() {
		return sessionId;
	}
	
	public void setView(AcpView view) {
		this.view = view;
		this.browser = view.getBrowser();
	}
	
	public IAgentService getAgent() {
		return agent;
	}

	//------------------------
	// AgentNotification
	//------------------------
	@Override
	public void accept(SessionNotification notification) {
		
		if (!sessionId.equals(notification.sessionId())) {
			return;
		}

		session.add(notification);
		
		
		if (notification.update() instanceof SessionUserMessageChunk) {
			browser.acceptSessionUserMessageChunk(
					((SessionUserMessageChunk)notification.update()).content());
		} else if (notification.update() instanceof SessionAgentThoughtChunk) {
			browser.acceptSessionAgentThoughtChunk(
					((SessionAgentThoughtChunk)notification.update()).content());
		} else if (notification.update() instanceof SessionAgentMessageChunk) {
			browser.acceptSessionAgentMessageChunk(
					((SessionAgentMessageChunk)notification.update()).content());
		} else if (notification.update() instanceof SessionToolCall) {
			SessionToolCall toolCall = (SessionToolCall)notification.update();
			browser.acceptSessionToolCall(
					toolCall.toolCallId(), 
					toolCall.title(), 
					toolCall.kind().toString(), 
					toolCall.status().toString());

		} else if (notification.update() instanceof SessionToolCallUpdate) {
			SessionToolCallUpdate toolCall = (SessionToolCallUpdate)notification.update();
			browser.acceptSessionToolCallUpdate(
					toolCall.toolCallId(), 
					toolCall.status().toString());
		}
		else if (notification.update() instanceof SessionPlan) {
			PlanEntry[] entries = ((SessionPlan)notification.update()).entries();
			for (int i = 1; i <= entries.length; i++) {
				if (entries[i].status() == PlanEntryStatus.in_progress) {
					System.err.println("Step " + i + " of " + (entries.length + 1) + ": " + entries[i].content());
				}
			}
		}
		else if (notification.update() instanceof SessionAvailableCommandsUpdate) {
			System.err.println(SessionAvailableCommandsUpdate.class.getCanonicalName());
		}
		else if (notification.update() instanceof SessionModeUpdate ) {
			System.err.println(SessionModeUpdate.class.getCanonicalName());
		}
	}

	//------------------------
	// AgentRequest
	//------------------------
	@Override
	public void accept(WriteTextFileRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(ReadTextFileRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(RequestPermissionRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(CreateTerminalRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(TerminalOutputRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(ReleaseTerminalRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(WaitForTerminalExitRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(KillTerminalCommandRequest request) {
		// TODO Auto-generated method stub
		
	}

	//------------------------
	// AgentResponse
	//------------------------
//	@Override
//	public void accept(InitializeResponse response) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void accept(AuthenticateResponse response) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void accept(NewSessionResponse response) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void accept(LoadSessionResponse response) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void accept(SetSessionModeResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(PromptResponse response) {
		TextBlock error = null;
		
		switch (((PromptResponse)response).stopReason()) {
		case cancelled:
			error = new TextBlock (null, null, "\nThe exchange has been canceled", "text");
			break;
		case end_turn:
			// the turn has ended normally
			break;
		case max_tokens:
			error = new TextBlock (null, null, "\nThe maximum token limit has been reached", "text");
			break;
		case max_turn_requests:
			error = new TextBlock (null, null, "\nThe maximum number of model requests in a single turn has been exceeded", "text");
			break;
		case refusal:
			error = new TextBlock (null, null, "\nThe Agent has refused to continue", "text");
			break;
		default:
			break;
		
		}
		
		if (error != null) {
			browser.acceptSessionAgentMessageChunk(error);
		}
		
		view.prompTurnEnded();
	}

	//------------------------
	// AgentResponse
	//------------------------
	@Override
	public void accept(CancelNotification notification) {
		// TODO Auto-generated method stub
	}

	//------------------------
	// ClientNotification
	//------------------------
//	@Override
//	public void accept(InitializeRequest request) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void accept(AuthenticateRequest request) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void accept(NewSessionRequest request) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void accept(LoadSessionRequest request) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void accept(SetSessionModeRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(PromptRequest request) {
		browser.acceptPromptRequest(request);
		view.prompTurnStarted();
	}

	//------------------------
	// ClientResponse
	//------------------------
	@Override
	public void accept(WriteTextFileResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(ReadTextFileResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(RequestPermissionResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(CreateTerminalResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(TerminalOutputResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(ReleaseTerminalResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(WaitForTerminalExitResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(KillTerminalCommandResponse response) {
		// TODO Auto-generated method stub
		
	}
}
