package org.eclipse.mcp;

/**
 * TODO
 */
public class MCPException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MCPException(Exception e) {
		super(e.getLocalizedMessage(), e);
	}
}
