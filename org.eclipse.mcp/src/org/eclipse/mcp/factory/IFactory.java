package org.eclipse.mcp.factory;

/**
 * Interface for components contributing MCP functionality
 */
public interface IFactory {
	/**
	 * @return Unique identifier used to track enablement preferences. Default to class cannonical name
	 */
	public default String getId() {
		return getClass().getCanonicalName();
	}
}


