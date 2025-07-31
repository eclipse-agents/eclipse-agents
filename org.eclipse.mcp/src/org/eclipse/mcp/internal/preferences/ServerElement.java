package org.eclipse.mcp.internal.preferences;

public interface ServerElement {
	public String getId();
	public String getName();
	public String getCategory();
	public String getDescription();
	public boolean isValid();
}
