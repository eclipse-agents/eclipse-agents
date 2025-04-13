# Prototyping in Progress: Implementation of a Model Context Protocol Server that runs inside Eclipse on localhost

- Adaptation of the stand-alone [Db2 for z/OS MCP prototype](https://github.ibm.com/jflicke/ibmz-mcp) that:
  - Runs inside the Eclipse process and can invoke internal Eclipse and IDz APIs
    - Exposes tools that re-use the Remote Systems connections defined in the active workspace
      - Connectivity re-uses the Eclipse secure storage and RSE authentication mechanisms
    - Enables chat to invoke tools to:
      - list available Db2 for z/OS Connections
      - list the schemas for those connections (w/ Autoconnect)
      - Run a read-only SQL query and display the results:
        - in the chat session
        - in the Remote Systems Details table
- Serves an MCP Server on localhost using SSE transport as a Jetty Servletcan run inside of IBM Developer for z and expose tools from its live process
- Uses Jetty to serve MCP Server as servlet, as described in [MCP Client and Server with the Java MCP SDK and LangChain4j](https://glaforge.dev/posts/2025/04/04/mcp-client-and-server-with-java-mcp-sdk-and-langchain4j)
- Content served on localhost using HttpServletSseServerTransportProvider
- When Developer for z is running, you can add to an MCP Client as such Claude as follows:

```json
{
  "mcpServers": {
    "IDz-DB2-MCP": {
      "command": "npx",
      "args": [
        "mcp-remote",
        "http://127.0.0.1:45450/sse"
      ]
    }
  }
}
```

## Potential Use Cases

- MCP Server endpoint(s) could be made public to be be consumed by:
  - other Eclipse Extensions that eventually become MCP Clients...
    - On the roadmap for Watson Code Assistant and others
  - LLM Models that support tools such as Granite 3.2 (requires explicity prompting at moment)
  - MCP clients outside of Eclipse, such as Claude Desktop
- Or potentially it could be exposed privately via an extension point contributed by say Watson Code Assistant
  - This could enable automatic detection / configuration of available MCP tools by say WCA
- Could be used to add context to Chat and Content Assist, for example what tables, columns, foreign keys are in scope for SQL generation for the active editor's active DB connection.
  - local caching of SYSIBM metadata can be incorporated, say via sqllite
- Could be used to trigger IDE commands/actions from Chat

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
