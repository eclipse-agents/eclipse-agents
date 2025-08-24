# Eclipse Plug-In Developer Extension for Model Context Protocol Services

The [org.eclipse.mcp.modelContextProtocolServer extension point](https://pages.github.ibm.com/jflicke/eclipse-mcp/org.eclipse.mcp/docs/modelContextProtocolServer.html) can be used to declare and instantiate Model Context Protocol (MCP) servers that run within the Eclipse IDE's VM enable interactivity between Eclipse based experiences and LLM-powered Agentic experiences running within or outside of the Eclipse IDE.

It provides a simple mechanism to contribute MCP Tools and Resources to an MCP server running inside Eclipse.

Optional access to the underlying [modelcontextprotocol/java-sdk](https://github.com/modelcontextprotocol/java-sdk) elements are available

Provides a centralized location for users customize Eclipse MCP capabilities and preferences.

The built-in MCP Server makes available a suite MCP resources, templates and tools accessing the Eclipse platform, enabling agents to interact with your Eclipse IDEs workspace files, editors, consoles, problems and tasks.

## Documentation

- [Extension Point Documentation](https://pages.github.ibm.com/jflicke/eclipse-mcp/org.eclipse.mcp/docs/modelContextProtocolServer.html)
- [Java Docs](https://pages.github.ibm.com/jflicke/eclipse-mcp/org.eclipse.mcp/docs/javadoc/org/eclipse/mcp/package-summary.html)
- [Platform MCP Tools](https://pages.github.ibm.com/jflicke/eclipse-mcp/org.eclipse.mcp.builtins/doc/org/eclipse/mcp/builtins/tools/BuiltinAnnotatedToolsFactory.html#method-summary)
- [Platform Resource Templates](https://pages.github.ibm.com/jflicke/eclipse-mcp/org.eclipse.mcp.builtins/doc/org/eclipse/mcp/builtins/tools/BuiltinAnnotatedToolsFactory.html#method-summary)
- [Platform Resource Factories](https://pages.github.ibm.com/jflicke/eclipse-mcp/org.eclipse.mcp.builtins/doc/org/eclipse/mcp/builtins/tools/BuiltinAnnotatedToolsFactory.html#method-summary)

## Demonstrations

- [Claude Conversation: What's wrong with my java project](https://claude.ai/share/31968356-df7e-471b-8fec-3b85868a2376)
- [Demo for Plug-in Developers](https://ibm.box.com/s/s6nc9n1nlpi4uiuzl7jpo4x6ra25zrk5)
- [Demo for Eclipse and IDz Users](https://ibm.box.com/s/sg4aq3w723cp7a7i75rdj0l1dgm3txg0)
- [Early prototype of Eclipse and Chat interactivity](https://ibm.box.com/s/cv4dnrvm6heapmu0c1amucs9l177fvrh)

Update your Eclipse plugin to contribute your own MCP tool and resource controllers to the platform in a few steps

IT also adds a new "Platform MCP" preference page will let users:

- Enable / Disable Platform MCP Server
- Set the HTTP Port
- Copy the URL to clipboard
- Turn on/off capabilities/activities for different categories of MCP tools, resources and prompts
- Add custom preference pages under the MCP preference page to toggle your plugin's behavior and contributions

To expose an aspect of your IDE plugin as an MCP tool, do the following:

## Getting Started:  Lets create an MCP Tool using an Annotated method

1. Create or open an Eclipse Plugin Project
2. Add plugins `org.eclipse.mcp` and `io.modelcontextprotocol` as dependencies to your plugin
3. Create a class that implements [MCPAnnotatedToolFactory.java](https://pages.github.ibm.com/jflicke/eclipse-mcp/org.eclipse.mcp/docs/javadoc/org/eclipse/mcp)

```java
public class MyToolFactory extends MCPAnnotatedToolFactory {

	@Tool (description = "Return the compilation issues for a file")
	public String[] getProblems(
		@ToolArg(description="The full path to the file to return prblems for")	String filePath) {
		
		List<String> result = new ArrayList<String>();
		
		/** Populate results with problems **/

		return result.toArray(String[]::new);
	}
}
```

4. Implement the logic for your tool, returning an array of Strings as your result

## Declare your tool as an extension in your plugin.xml

1. Using the Plugin Manifest Editor, add to your plugin.xml
    1. Add the 'org.eclipse.mcp.modelContextProtocolServer' extension
    2. Add the the extension a contributor
    3. Add to the contributor a factory

Example:

```xml
  <extension
         point="org.eclipse.mcp.modelContextProtocolServer"
         id="foo.com.my.extension.id"
         name="My MCP Extension">

      <contributor
         activityId="foo.com.my.activity.id">
         <factory class = "foo.com.MyToolFactory"/>
      </contributor>
   </extension>
```

Thats all that is required.  Upon startup, MCP servers will start up and serve content over HTTP for the registered tools.  Calls to tools will be delegated to your instances of IMCPTool

2. The infrastructure will handle generation of JSON input schema and conversion betwen JSON and basic Java types and 1-dimensional Arrays
    1. For usage details, see: [IMCPToolFactory](https://pages.github.ibm.com/jflicke/eclipse-mcp/org.eclipse.mcp/docs/javadoc/org/eclipse/mcp/IMCPToolFactory.html) 
3. Declare your toolFactory as an org.eclipse.mcp.modelContextProtocolServer extension in your plugin.xml


### Future Considerations

1. Mechanism for shell-shared MCP Clients to invoke MCP tools as Java calls rather than HTTP calls, removing the need for HTTP endpoints when consumed internally and hand registration of MCP endpoints into co-installed MCP clients.
2. Preferences to  
    1. Button to copy Server URL to clip-board
    2. Button for opening Tool-specific property editors
        1. For example, set the default Db2 Connection to use when running a SQL query.
    3. ?Option to customize a Tools name/prompt
3. Set of Built-in Tools and Examples, such as
    1. Access to Problems
    2. Access to Consoles
    3. Access to Editors

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