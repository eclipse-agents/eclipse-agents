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
package org.eclipse.mcp.acp;

import org.eclipse.mcp.acp.protocol.AcpSchema.CancelNotification;
import org.eclipse.mcp.acp.protocol.AcpSchema.CreateTerminalRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.CreateTerminalResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.KillTerminalCommandRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.KillTerminalCommandResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.PromptRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.PromptResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.ReadTextFileRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.ReadTextFileResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.ReleaseTerminalRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.ReleaseTerminalResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.RequestPermissionRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.RequestPermissionResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.SessionNotification;
import org.eclipse.mcp.acp.protocol.AcpSchema.SetSessionModeRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.SetSessionModeResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.TerminalOutputRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.TerminalOutputResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.WaitForTerminalExitRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.WaitForTerminalExitResponse;
import org.eclipse.mcp.acp.protocol.AcpSchema.WriteTextFileRequest;
import org.eclipse.mcp.acp.protocol.AcpSchema.WriteTextFileResponse;

public interface IAcpSessionListener {

	public String getSessionId();

	//AgentNotification
	public void accept(SessionNotification notification);
	
	//AgentRequest
	public void accept(WriteTextFileRequest request);
	public void accept(ReadTextFileRequest request);
	public void accept(RequestPermissionRequest request);
	public void accept(CreateTerminalRequest request);
	public void accept(TerminalOutputRequest request);
	public void accept(ReleaseTerminalRequest request);
	public void accept(WaitForTerminalExitRequest request);
	public void accept(KillTerminalCommandRequest request);
	
	//AgentResponse
//	public void accept(InitializeResponse response);
//	public void accept(AuthenticateResponse response);
//	public void accept(NewSessionResponse response);
//	public void accept(LoadSessionResponse response);
	public void accept(SetSessionModeResponse response);
	public void accept(PromptResponse response);
	
	//ClientNotification
	public void accept(CancelNotification notification);
	
	//ClientRequest
//	public void accept(InitializeRequest request);
//	public void accept(AuthenticateRequest request);
//	public void accept(NewSessionRequest request);
//	public void accept(LoadSessionRequest request);
	public void accept(SetSessionModeRequest request);
	public void accept(PromptRequest request);
	
	//ClientResponse
	public void accept(WriteTextFileResponse response);
	public void accept(ReadTextFileResponse response);
	public void accept(RequestPermissionResponse response);
	public void accept(CreateTerminalResponse response);
	public void accept(TerminalOutputResponse response);
	public void accept(ReleaseTerminalResponse response);
	public void accept(WaitForTerminalExitResponse response);
	public void accept(KillTerminalCommandResponse response);
}
