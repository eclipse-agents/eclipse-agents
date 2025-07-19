# WIP: Eclipse MCP Extenion Point

To expose an aspect of your IDE plugin as an MCP tool, do the following:

## Implement your MCP Tool

1. Create a class that implements 'IModelContextProtocolTool'

```java
public interface IModelContextProtocolTool {
  /** 
   * @param The tools input parameters as defined by its declared JSON Schema
   * @return An array of strings representing the result of the tool execution.
   */
  public String[] apply(Map<String, Object> args);
}
```

2. Define a JSON schema that describes the arguments your tool will accept, for example

```json
{
  "type": "object",
  "properties": {
    "connection-uuid": {
      "type": "string"
    },
    "query": {
      "type": "string"
    }
  },
  "required": ["connection-uuid", "query"]
}
```

3. Implement the logic for your tool, returning an array of Strings as your result

## Declare your tool as an extension in your plugin.xml

1. Using the Plugin Manifest Editor, add to your plugin.xml
    1. Add the 'org.eclipse.mcp.modelContextProtocolServer' extension
    2. Add to the extension an MCP Server with a name, id, description and default http port
    3. Add the the extension 1 or more tools, each with a name, id, description and JSON Schema input, and implementaton class.
        1. Agents will use the descirption and input schema to determine how and when to call your tool
        2. The JSON schema must be XML Escaped.
            1. Enter the value in the Plugin Editor, it will automatically escape the JSON schema for you.
    4. Add a binding between your tools and a server

Example:

```xml
 <extension
         point="org.eclipse.mcp.modelContextProtocolServer">
      <server
            description="Set of default tools for enabling Agent-driven development"
            id="org.eclipse.mcp.builtins"
            name="Built-in Eclipse MCP Server"
            version="0.0.1"
            defaultPort="12931">
      </server>
      <tool
            class="org.eclipse.mcp.builtin.tool.ListConsoles"
            id="org.eclipse.mcp.builtin.tool.ListConsoles"
            name="listConsoles"
            description="List the open Eclipse consoles"
            schema="{
				&quot;type&quot;: &quot;object&quot;,
				&quot;properties&quot;: {
				},
				&quot;required&quot;: []
			}">
      </tool>
      <toolServerBinding
            serverId="org.eclipse.mcp.builtins"
            toolId="org.eclipse.mcp.builtin.tool.ListConsoles">
      </toolServerBinding>
   </extension>
```

Thats all that is required.  Upon startup, MCP servers will start up and serve content over HTTP for the registered tools.  Calls to tools will be delegated to your instances of IModelContextProtocolTool

### Future Considerations

1. Mechanism for shell-shared MCP Clients to invoke MCP tools as Java calls rather than HTTP calls, removing the need for HTTP endpoints when consumed internally.
2. Preferences to  
    1. Enable/Disable MCP Servers and Tools
    2. Override the default HTTP port
    3. Option to enable/disable exposing server over HTTP
    4. Button to copy Server URL to clip-board
    5. Button for opening Tool-specific property editors
        1. For example, set the default Db2 Connection to use when running a SQL query.
    6. ?Option to customize a Tools name/prompt
3. Support for Resource Management extension point
    1. Templating support
4. Set of Built-in Tools, such as
    1. Access to Problems
    2. Access to Consoles
    3. Access to Editors

#### Demo: Creating a simple MCP Tool in an Eclipse Plugin

1. [Snapshot Demonstration of a plugin contributing an MCP Server and Tool](https://ibm.box.com/s/s6nc9n1nlpi4uiuzl7jpo4x6ra25zrk5)

### Demo: IBM Developer for z's Db2 for z/OS tooling interacting with Chat over MCP

- [List Connections, List Schemas, Run Query in Chat, Run Query in Table](https://ibm.box.com/s/cv4dnrvm6heapmu0c1amucs9l177fvrh)

### References

- [Model Context Protocol](https://www.anthropic.com/news/model-context-protocol)
- [java sdk](https://github.com/modelcontextprotocol/java-sdk)
- [spring sdk](https://docs.spring.io/spring-ai-mcp/reference/mcp.html)
- [java sdk jar](https://mvnrepository.com/artifact/io.modelcontextprotocol.sdk/mcp/0.8.1)
- [quarkus examples](https://github.com/quarkiverse/quarkus-mcp-servers/tree/main/jdbc)
- [spring mcp](https://github.com/spring-projects-experimental/spring-ai-mcp)
- [MCP Client and Server with the Java MCP SDK and LangChain4j](https://glaforge.dev/posts/2025/04/04/mcp-client-and-server-with-java-mcp-sdk-and-langchain4j)

### Examples of Agentic IDE Tooling

- [Claude Code API Applied to other IDEs](https://github.com/anthropics/claude-code/issues/1234)
- [Windsurf Flow Awareness](https://windsurf.com/blog/windsurf-wave-9-swe-1)
- [The Hidden Algorithms Powering Your Coding Assistant](https://diamantai.substack.com/p/the-hidden-algorithms-powering-your?utm_campaign=post)