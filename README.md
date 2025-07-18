# Prototyping in Progress: Implementation of a Model Context Protocol Server that runs inside Eclipse on localhost

Prototype for running MCP servers within an Eclipse instance which can expose resources and tooling to Chat and Agent experiences within and/or outside of the Eclipse workbench.

MCP servers can be shared externally over http, or consumed internally using Java function calls.

## org.eclipse.mcp

- contributes extension point org.eclipse.mcp.modelContextProtocolServer
  - lets you define MCP Servers
    - specify a name, description and defaultHttpPort
  - lets you define MCP tools
    - lets you define the name, description and input schema for your tool
    - lets you specify the implementation, an instance of org.eclipse.mcp.IModelContextProtocolTool
  - lets you associate an MCP tool with an MCP server
- provides abstraction for MCP Tool implementation
  - Implement instance of org.eclipse.mcp.IModelContextProtocolTool
    - implement its sole method: String[] apply(Map<String, Object> args);
      - accepts as input a Map that matches the input schema declared in the extension
- Manages setup/shutdown of Synchronized MCP Servers for those registered
- Manages registration of tools and forwards calls to your implementation
- [WIP]
  - Preferences page to:
    - Enable/Disable MCP Servers and Tools
    - Override the default HTTP port
    - Option to enable/disable exposing server over HTTP
      - Alternatively, clients running within the Eclipse instance can use the registry to invoke tools
    - Button to copy Server URL to clip-board
    - Options to open a Tool-specific preferences pages.
      - For example, set the default Db2 Connection to use when running a SQL query.
    - ?Option to customize a Tools name/prompt
      - Advanced users may want to refine to affect when an Agent invokes the tool and the arguments it chooses to forward
  - Support for Resource Manager extension point
    - Implementations can dynamically add and remove Resources from scope based on user activity
    - ?Support for delegating Resource Templating resolution

## Users

- Software Developers
- SWE Coding Agents

### Built-in Set of Eclipse Tools [WIP]

- Grant access to Problems
  - Grant access to problem source file, line number, location
  - Grant access per file / project / workspace
  - Grant access to Problem Quick Fixes
  - Grant access to editor annotations
- Grant access to Console (Program I/O)
  - Grant access to logs of running applications
  - Grant access to Exceptions / Errors / Stack Traces


### Built-in Set of Eclipse Resources [WIP]
- Resources of open editors
  - Resource of active editor
- ?Resources of console tabs

## IBM Product Extensions

### IBM Developer for System z [WIP]

- Tools
  - Outline Summary
  - Compilation Unit Denormalization
  - Incorporate Git MCP
  - Incorporate Db2 for z/OS MCP
    - Run read-only query
    - Explorer catalog metadata
  - Incorporate RSE MCP

## Thoughts [WIP]

- org.eclipse.mcp plugin to be a registry and configuration hub for MCP server contributions from shell-shared extensions/plugins
  - Includes the jar dependencies
  - Abstract classes for easy tool, resource implementations
  - Extension point for 3rd parties to contribute
    - Plugins can register MCP components using Eclipse extension points
    - Shell shared MCP hosts such as WCA, Copilot can detect and auto-configure in IDE MCP servers
    - Users would then not need to mess with customizing JSON config files.
      - Could shell-shared clients re-use MCP java natively w/o needing HTTP ports?
  - to ship with a set of built in tools and resources for base eclipse features
    - editor access
    - problem access
    - console access
    - recent context history
  - Preference page to customize registered tools and resources
    - Preferences to enable/hide MCP servers
    - Preference to enable/hide MCP tools, resource contributors, prompts
    - Preference to expose MCP server over HTTP on a port.
    - Copy button to copy a servers URL to clip-board
    - Maybe a preference to customize the description/prompt of a tool?
      - LLM Agent will use tools names and descriptions to determine when to call and what to forward as arguments
    - Some preferences are specific to the contributed component
    - A button to open a Properties page of server-specific preferences
    - customize resource contribution behaviors
    - A button to open a Properties page of tool-specific preferences
- IBM and third party Plugins can potentially contribute tools, prompts, resources to MCP servers from other plugins
  - Compilation Unit of COBOL or PL/I program
  - Resource for active editor’s variables / outline content
  - Resource for active editor’s denormalized content (copy/include resolution)
  - MVS, USS, JES
    - re-use IDz's secure authentication  mechanisms
  - Db2 for z/OS
    - re-use IDz's secure authentication  mechanisms
    - Run (read-only) query
    - Retrieve catalog metadata


```json
{
  "eclipse-mcp": {
    "eclipse-mcp": {
      "command": "npx",
      "args": [
        "mcp-remote",
        "http://127.0.0.1:45450/sse"
      ]
    }
  }
}
```

## To use

1. [Snapshot Demonstration of a plugin contributing an MCP Server and Tool(https://ibm.box.com/s/s6nc9n1nlpi4uiuzl7jpo4x6ra25zrk5)

### Demonstration of IBM Developer for z's Db2 for z/OS tooling interacting with Chat over MCP

- [List Connections, List Schemas, Run Query in Chat, Run Query in Table](https://ibm.box.com/s/cv4dnrvm6heapmu0c1amucs9l177fvrh)

### References

- [Model Context Protocol](https://www.anthropic.com/news/model-context-protocol)
- [java sdk](https://github.com/modelcontextprotocol/java-sdk)
- [spring sdk](https://docs.spring.io/spring-ai-mcp/reference/mcp.html)
- [java sdk jar](https://mvnrepository.com/artifact/io.modelcontextprotocol.sdk/mcp/0.8.1)
- [quarkus examples](https://github.com/quarkiverse/quarkus-mcp-servers/tree/main/jdbc)
- [spring mcp](https://github.com/spring-projects-experimental/spring-ai-mcp)
- [MCP Client and Server with the Java MCP SDK and LangChain4j](https://glaforge.dev/posts/2025/04/04/mcp-client-and-server-with-java-mcp-sdk-and-langchain4j)

### API Examples in the field

- [Claude Code API Applied to other IDEs](https://github.com/anthropics/claude-code/issues/1234)
- [Windsurf Flow Awareness](https://windsurf.com/blog/windsurf-wave-9-swe-1)
- [The Hidden Algorithms Powering Your Coding Assistant](https://diamantai.substack.com/p/the-hidden-algorithms-powering-your?utm_campaign=post)

### Prior Examples

- [Db2 for z/OS MCP prototype](https://github.ibm.com/jflicke/ibmz-mcp) that can run inside of IBM Developer for z and expose tools from its live process
