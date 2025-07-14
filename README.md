# Prototyping in Progress: Implementation of a Model Context Protocol Server that runs inside Eclipse on localhost

Prototype for running MCP servers within an Eclipse instance which can expose resources and tooling to Chat and Agent experiences within and/or outside of the Eclipse workbench.

MCP servers can be shared externally over http, or consumed internally using Java function calls.

## Users:

- Software Developers
- SWE Coding Agents

### Eclipse Tools [WIP]

- Grant access to Problems
  - Grant access to problem source file, line number, location
  - Grant access per file / project / workspace
  - Grant access to Problem Quick Fixes
  - Grant access to editor annotations
- Grant access to Console (Program I/O)
  - Grant access to logs of running applications
  - Grant access to Exceptions / Errors / Stack Traces

### Eclipse Resources [WIP]

- Incorporate File Access
  - Incorporate Indexed, Workspace Search
  - Consideration for resource templating

## IBM Product Extensions

### IBM Developer for System z [WIP]

- Tools
  - Outline Summary
  - Compilation Unit Denormalization
  - Incorporate Git MCP
  - Incorporate Db2 for z/OS MCP
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
    - Preference to enable/hid MCP tools, resource contributors, prompts
    - Preference to expose MCP server over HTTP on a port.
    - Maybe a preference to customize the description/prompt of a tool?
    - Some preferences are specific to the contributed component
    - A button to open a Properties page of server-specific preferences
    - customize resource contribution behaviors
    - A button to open a Properties page of tool-specific preferences
- IBM and third party Plugins could potentially contribute tools, prompts, resources to MCP servers from other plugins
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

1. Don't think you can at this time.

### Demonstration of IBM Developer for z's Db2 for z/OS tooling interacting with Chat over MCP:

- [List Connections, List Schemas, Run Query in Chat, Run Query in Table](https://ibm.box.com/s/cv4dnrvm6heapmu0c1amucs9l177fvrh)

### References

- [Model Context Protocol](https://www.anthropic.com/news/model-context-protocol)
- [java sdk](https://github.com/modelcontextprotocol/java-sdk)
- [spring sdk](https://docs.spring.io/spring-ai-mcp/reference/mcp.html)
- [java sdk jar](https://mvnrepository.com/artifact/io.modelcontextprotocol.sdk/mcp/0.8.1)
- [quarkus examples](https://github.com/quarkiverse/quarkus-mcp-servers/tree/main/jdbc)
- [spring mcp](https://github.com/spring-projects-experimental/spring-ai-mcp)
- [MCP Client and Server with the Java MCP SDK and LangChain4j](https://glaforge.dev/posts/2025/04/04/mcp-client-and-server-with-java-mcp-sdk-and-langchain4j)
- [Db2 for z/OS MCP prototype](https://github.ibm.com/jflicke/ibmz-mcp) that can run inside of IBM Developer for z and expose tools from its live process
