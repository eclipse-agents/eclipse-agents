# Implementation of a Model Context Protocol Server that runs inside Eclipse on localhost

- Adaption of the stand-alone [Db2 for z/OS MCP prototype](https://github.ibm.com/jflicke/ibmz-mcp) that can run inside of IBM Developer for z and expose tools from its live process

- Uses Jetty to serve MCP Server as servlet, as described in [MCP Client and Server with the Java MCP SDK and LangChain4j](https://glaforge.dev/posts/2025/04/04/mcp-client-and-server-with-java-mcp-sdk-and-langchain4j) 
- Exports tools for:
  - list the Db2 for z/OS Connections defined in Remote Systems View
  - list the schemas for a connection
  - run a query for a connection
    - example that returns results to MCP client
    - example that displays results in Remote Systems Details View.
- Content served on localhost using HttpServletSseServerTransportProvider
- When Developer for z is running, you can add to an MCP Client as such Claude as follows:
```
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
