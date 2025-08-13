package org.eclipse.mcp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mcp.annotated.MCPAnnotatedToolFactory;

public interface IMCPFactory {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Tool {
		/**
		 * Unique identifier reference-able in <code>org.eclipse.mcp.modelContextProtocolServer</code> extension
		 * @return
		 */
	    String id() default "";
	    /**
	     * Agent-presentable name for this  MCP Tool
	     * @return
	     */
	    String name() default "";
	    /**
	     * Agent-presentable description for this MCP Tool
	     * @return
	     */
	    String description();
	    /**
	     * Optional reference to a <code>org.eclipse.mcp.modelContextProtocolServer</code> category
	     * @return
	     */
	    String category() default "";
	    
	    String contributor() default "";
	    String inputSchema() default "";
	    String outputSchema() default "";
	    String title() default "";
	    boolean readOnlyHint() default false;
	    boolean destructiveHint() default false;
	    boolean idempotentHint() default false;
	    boolean openWorldHint() default false;
	    boolean returnDirect() default false;

	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	public @interface ToolArg {
		/**
		 * Agent-presentable name for this Tool argument
		 * @return
		 */
	    String name();
	    /**
		 * Agent-presentable description for this Tool argument
		 * @return
		 */
	    String description();
	    boolean required() default true;
	}
	
	public default IMCPToolFactory[] createToolFactories() {
		List<IMCPToolFactory> tools = new ArrayList<IMCPToolFactory>();
		for (Method method: getClass().getDeclaredMethods()) {
			Tool tool = method.getAnnotation(Tool.class);
			if (tool != null) {
				MCPAnnotatedToolFactory toolFactory = createMCPAnnotatedToolFactoryMcpTool(this, method, tool);
				if (toolFactory.isValid()) {
					tools.add(toolFactory);
				}
			}
		}
		return tools.toArray(MCPAnnotatedToolFactory[]::new);
	}
	
	public default MCPAnnotatedToolFactory createMCPAnnotatedToolFactoryMcpTool(IMCPFactory factory, Method method, Tool toolAnnotation) {
		return new MCPAnnotatedToolFactory(factory, method, toolAnnotation);
	}
	
	public default IMCPResourceFactory[] createResourceFactories() {
		return new IMCPResourceFactory[0];
	}
	
	public default IMCPResourceTemplateFactory[] createResourceTemplateFactories() {
		return new IMCPResourceTemplateFactory[0];
	}
}


