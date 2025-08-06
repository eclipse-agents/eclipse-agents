package org.eclipse.mcp.test.junit.plugin.extension;

import java.util.Map;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.mcp.IElementProperties;
import org.eclipse.mcp.IMCPTool;


/*
{
  "type": "object",
  "properties": {
    "name": {
      "type": "string"
    }
  },
  "required": ["name"]
}
*/



public class MCPTool implements IMCPTool {

	@Override
	public String[] apply(Map<String, Object> args, IElementProperties properties) {
		
		IDialogSettings  settings = properties.getProperties(MCPTool.class.getCanonicalName());
		return new String[] { 
				"Hello " + args.get("name"),
				"Goodbye " + settings.get("name")
		};
	}

}