package org.eclipse.mcp.experimental.annotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.mcp.MCPException;
import org.eclipse.mcp.factory.ToolFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.ToolAnnotations;


/**
 * Conveniences for creating one or more ToolFactories from annotated Java methods.
 * A JSON Input schema for your method's arguments will be constructed and basic mappings to/from JSON
 * will be handled.  Arguments may be of types Boolean,Character, Double, Float, Integer, Long, Short, String[] Integer[]
 * No JSON Output Schema is generated at this time
 */
public class MCPAnnotatedToolFactory extends ToolFactory {
	
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
	

	public static ToolFactory[] createToolFactories(Class<?>[] classes) throws MCPException {
		List<ToolFactory> tools = new ArrayList<ToolFactory>();
		for (Class<?>c: classes) {
			tools.addAll(Arrays.asList(createToolFactories(c)));
		}
		return tools.toArray(new ToolFactory[0]);
	}


	public static ToolFactory[] createToolFactories(Class<?> c) throws MCPException {
		List<ToolFactory> tools = new ArrayList<ToolFactory>();
		for (Method method: c.getDeclaredMethods()) {
			Tool tool = method.getAnnotation(Tool.class);
			if (tool != null) {
				try {
					Constructor co = c.getConstructor(Method.class, Tool.class);
					MCPAnnotatedToolFactory toolFactory = (MCPAnnotatedToolFactory)co.newInstance(method, tool);
					if (toolFactory.isValid()) {
						tools.add(toolFactory);
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return tools.toArray(new ToolFactory[0]);
	}

	Object instance;
	Method method;
	Tool toolAnnotation;
	String inputSchema;
	String outputSchema;
	ListenerList listeners = new ListenerList();

	public MCPAnnotatedToolFactory(Method method, Tool toolAnnotation) {
		super();
		this.instance = this;
		this.method = method;
		this.toolAnnotation = toolAnnotation;
		
		inputSchema = createInputSchema();
		outputSchema = createOutputSchema();
	}

	public String createInputSchema() {

		if (!toolAnnotation.inputSchema().isEmpty()) {
			return toolAnnotation.inputSchema();
		}
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode result = mapper.createObjectNode();
		ObjectNode properties = mapper.createObjectNode();
		ArrayNode required = mapper.createArrayNode();
		
		result.put("type", "object");
		result.set("properties", properties);
		result.set("required",  required);

		for (Parameter parameter: method.getParameters()) {
			ToolArg arg = parameter.getAnnotation(ToolArg.class);
			if (arg != null) {
				String name = arg.name();
				String description = arg.description();
				boolean req = arg.required();

				JsonNode node = generateJsonSchema(parameter.getType());
				
				if (!description.isEmpty() && node instanceof ObjectNode) {
					((ObjectNode)node).put("description", description);
				}
				properties.set(name, node);
				if (req) {
					required.add(name);
				}
				
			}	
		}
		return result.toString();
	}
	
	public String createOutputSchema() {
		if (!toolAnnotation.outputSchema().isEmpty()) {
			return toolAnnotation.outputSchema();
		}
		return generateJsonSchema(method.getReturnType()).toString();
	}

	@Override
	public String getId() {
		if (toolAnnotation.id().isEmpty()) {
			return instance.getClass().getCanonicalName() + "." +  method.getName();
		}
		return toolAnnotation.id();
	}
	
	public String getName() {
		if (toolAnnotation.name().isEmpty()) {
			return method.getName();
		}
		return toolAnnotation.name();
	}

	public String getDescription() {
		return toolAnnotation.description();
	}

	public String getInputSchema() {
		return inputSchema.toString();
	}
	
	public boolean isValid() {
		return true;
	}
	
	

	@Override
	public McpSchema.Tool createTool() {
		ToolAnnotations annotations = new ToolAnnotations(
				toolAnnotation.title(),
				toolAnnotation.readOnlyHint(),
				toolAnnotation.destructiveHint(),
				toolAnnotation.idempotentHint(),
				toolAnnotation.openWorldHint(),
				toolAnnotation.returnDirect());
		
		return io.modelcontextprotocol.spec.McpSchema.Tool.builder()
				.annotations(annotations)
				.name(getName())
				.description(getDescription())
				.title(toolAnnotation.title())
				.inputSchema(createInputSchema())
//				.outputSchema(createOutputSchema())
				.build();
				
	}

	@Override
	public String[] apply(Map<String, Object> args) {
		List<Object> inputs = new ArrayList<Object>();
		for (Parameter param: method.getParameters()) {
			ToolArg arg = param.getAnnotation(ToolArg.class);
			String paramName = param.getName();
			if (arg != null && arg.name() != null) {
				paramName = arg.name();
			}
			Object casted = castArgumentToClass(args.get(paramName), param.getType());
			inputs.add(casted);
		}
		Object result = null;
		try {
			result = method.invoke(instance, inputs.toArray());
		} catch (IllegalAccessException e) {
			throw new MCPException(e);
		} catch (IllegalArgumentException e) {
			throw new MCPException(e);
		} catch (InvocationTargetException e) {
			throw new MCPException(e);
		}
		if (result instanceof String[]) {
			return (String[])result;
		}
		return null;
	}
	
	
	protected Object castArgumentToClass(Object value, Class<?> javaType) {
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.convertValue(value, JsonNode.class);
		Object cast = mapper.convertValue(node, javaType);
		return cast;
	}
}