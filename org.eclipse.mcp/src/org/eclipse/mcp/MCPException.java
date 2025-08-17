package org.eclipse.mcp;

import org.eclipse.core.runtime.IStatus;

/**
 * TODO
 */
public class MCPException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MCPException(String message) {
		super(message);
	}
	public MCPException(Exception e) {
		super(e.getLocalizedMessage(), e);
	}
	
	public MCPException(IStatus status) {
		super(status.getMessage(), status.getException());
	}
}
