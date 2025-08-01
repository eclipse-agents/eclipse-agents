package org.eclipse.mcp.internal.preferences;

import org.eclipse.core.runtime.IAdaptable;

public interface ServerElement extends IAdaptable {
	public String getId();
	public String getName();
	public String getCategory();
	public String getDescription();
	public boolean isValid();
	public String[] getPropertyEditorIds();
	public void addPropertyEditorId(String id);
}
