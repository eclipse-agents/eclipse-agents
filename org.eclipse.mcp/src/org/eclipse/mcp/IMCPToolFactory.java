package org.eclipse.mcp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mcp.internal.AnnotatedMCPTool;


/**
 * This interface enables tool contributors to automatically create and register any Annotated Java method as an IMCPTool.
 * Add this class to an <code>org.eclipse.mcp.modelContextProtocolServer</code> extension using the <code>toolFactory</code> element.
 * 
 * <ul><li>
 * Add the annotation <code>org.eclipse.mcp.IMCPToolFactory.Tool</code> to any method you want exposed as an IMCPTool
 * </li><li>
 * Add the method argument annotation <code>org.eclipse.mcp.IMCPToolFactory.ToolArg</code> to each argument
 * </li></ul>
 *  For example:
 *  * <pre>
 * {@code
 *  @Tool (id = "foo.bar.helloWorld", 
 *  	description = "Greets user with a hello", 
 *  	name = "e4-hello-world",
 *  	categoryId = "foo.bar.categoryId",
 *  	propertyPageIds = { "foo.bar.propertyPage1", "foo.bar.propertyPage1"})
 *  public String[] helloWorld(
 *  	@ToolArg(name = "firstName", description = "First name") String firstName,
 *  	@ToolArg(name = "age", description = "User's age", required = false) Integer age,
 *  	@ToolArg(name = "address", description = "User's address", required = false) String[] address) {	
 *  		return new String[] { "Hello " + firstName };
 *  }
 * }
 * </pre>
 * 
 * Notes: 
 * <ul>
 * <li>do not use primitive types for non-required parameters as the null value will fail conversion</li>
 * <li>Support for case Boolean, Character,String, Double, Float, Integer, Long, Short and 1-d arrays of these</li>
 * <li>You may optionally include an IElementProperties argument in your function without a <code>@ToolArg</code> annotations to retrieve any preferences saved from an associated propertyPageId</li>
 * <li>To bind tools to a default server, define categories, you will still contribute elements to your <code>org.eclipse.mcp.modelContextProtocolServer</code> extension</li>
 * </ul>
 */

//TODO see https://github.com/spring-projects-experimental/spring-ai-mcp/blob/main/spring-ai-mcp/src/main/java/org/springframework/ai/mcp/spring/ToolHelper.java
public interface IMCPToolFactory {

	public final static String[] DEFAULT_PROPERTYPAGE_IDS = new String[0];
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Tool {
		/**
		 * Unique identifier reference-able in <code>org.eclipse.mcp.modelContextProtocolServer</code> extension
		 * @return
		 */
	    String id();
	    /**
	     * Agent-presentable name for this  MCP Tool
	     * @return
	     */
	    String name();
	    /**
	     * Agent-presentable description for this MCP Tool
	     * @return
	     */
	    String description();
	    /**
	     * Optional reference to a <code>org.eclipse.mcp.modelContextProtocolServer</code> category
	     * @return
	     */
	    String categoryId() default "";
	    /**
	     * Optional references to one or more <code>org.eclipse.mcp.modelContextProtocolServer</code> propertyPages, enabling custom user-preference management for this tool
	     * @return
	     */
	    String[] propertyPageIds() default {};
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
	
	/**
	 * Alternative IMCPTool that is declared programmatically rather than thru a <code>org.eclipse.mcp.modelContextProtocolServer</code>  tool element 
	 */
	interface IMCPAnnotatedTool extends IMCPTool {
		
		/**
		 * @return Unique identifier reference-able in <code>org.eclipse.mcp.modelContextProtocolServer</code> extension
		 */
		public String getId();
		/**
		 * @return Agent-presentable description for this MCP Tool
		 */
		public String getName();
		/**
		 * @return Agent-presentable description for this MCP Tool
		 */
		public String getDescription();
		/**
		 * @return the JSON input schema for this MCP tool
		 */
		public String getInputSchema();
		/**
		 * @return Optional reference to a <code>org.eclipse.mcp.modelContextProtocolServer</code> category
		 */
		public String getCategoryId();
		/**
		 * @return Optional references to one or more <code>org.eclipse.mcp.modelContextProtocolServer</code> propertyPages, enabling custom user-preference management for this tool
		 */
	    public String[] getPropertyPageIds();
		//TODO
//		public String getOutputSchema();

		
	}
	
	/**
	 * The default implementation will scan the IMCPToolFactory for any methods annotated with <code>org.eclipse.mcp.IMCPToolFactory.Tool</code> and return IMCPTool implementations for thems
	 * @return
	 */
	public default IMCPAnnotatedTool[] createTools() {
		List<IMCPTool> tools = new ArrayList<IMCPTool>();
		for (Method method: getClass().getDeclaredMethods()) {
			Tool tool = method.getAnnotation(Tool.class);
			if (tool != null) {
				AnnotatedMCPTool annotatedTool = new AnnotatedMCPTool(this, method, tool);
				if (annotatedTool.isValid()) {
					tools.add(annotatedTool);
				}
			}
		}
		return tools.toArray(IMCPAnnotatedTool[]::new);
	}
}
