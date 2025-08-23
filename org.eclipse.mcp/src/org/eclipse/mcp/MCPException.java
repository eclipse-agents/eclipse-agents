package org.eclipse.mcp;

import org.eclipse.core.runtime.IStatus;

import io.modelcontextprotocol.spec.McpSchema.ErrorCodes;
import io.modelcontextprotocol.spec.McpSchema.JSONRPCResponse.JSONRPCError;
import io.modelcontextprotocol.spec.McpError;

/**
 * TODO
 */
public class MCPException extends McpError {

	private static final long serialVersionUID = 1L;

	public MCPException(String message) {
		super(new JSONRPCError(ErrorCodes.INTERNAL_ERROR, message, null));
	}

	public MCPException(Exception e) {
		super(new JSONRPCError(ErrorCodes.INTERNAL_ERROR, e.getLocalizedMessage(), e));
	}
	
	public MCPException(IStatus status) {
		super(new JSONRPCError(ErrorCodes.INTERNAL_ERROR, status.getMessage(), status.getException()));
	}
}
