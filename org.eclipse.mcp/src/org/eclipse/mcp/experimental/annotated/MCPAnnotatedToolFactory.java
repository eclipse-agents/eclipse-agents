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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.JsonSchema;
import io.modelcontextprotocol.spec.McpSchema.ToolAnnotations;


/**
 * Conveniences for creating one or more ToolFactories from annotated Java methods.
 * A JSON Input schema for your method's arguments will be constructed and basic mappings to/from JSON
 * will be handled.  Arguments may be of types Boolean,Character, Double, Float, Integer, Long, Short, String[] Integer[]
 * No JSON Output Schema is generated at this time
 */
public class MCPAnnotatedToolFactory extends ToolFactory {
	
	/**
	 * <a href="https://github.com/modelcontextprotocol/modelcontextprotocol/blob/main/schema/draft/schema.ts#L885">Tool Schema Reference</a>
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Tool {
	    /**
	     *	Intended for programmatic or logical use, but used as a display name in past specs or fallback (if title isn't present).
	     */
	    String name() default "";
	    /**
	     * This can be used by clients to improve the LLM's understanding of available tools. It can be thought of like a "hint" to the model.
	     */
	    String description();
	    /**
	     * A JSON Schema object defining the expected parameters for the tool.
	     */
	    String inputSchema() default "";
	    /**
	     * An optional JSON Schema object defining the structure of the tool's output returned in
	     * the structuredContent field of a CallToolResult.
	     */
	    String outputSchema() default "";
	    /**
	     * A human-readable title for the tool.   
	     * Display name precedence order is: title, annotations.title, then name.
	     */
	    String title();
	    /**
	     * If true, the tool does not modify its environment.
	     */
	    boolean readOnlyHint() default false;
	    /**
	     * If true, the tool may perform destructive updates to its environment.
	     * If false, the tool performs only additive updates.
	     *
	     * (This property is meaningful only when `readOnlyHint == false`)
	    */
	    boolean destructiveHint() default true;
	    /**
	     * If true, calling the tool repeatedly with the same arguments
	     * will have no additional effect on the its environment.
	     *
	     * (This property is meaningful only when `readOnlyHint == false`)
	     *
	     * Default: false
	     */
	    boolean idempotentHint() default false;
	    /**
	     * If true, this tool may interact with an "open world" of external
	     * entities. If false, the tool's domain of interaction is closed.
	     * For example, the world of a web search tool is open, whereas that
	     * of a memory tool is not.
	     *
	     * Default: true
	     */
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
	ListenerList listeners = new ListenerList();

	public MCPAnnotatedToolFactory(Method method, Tool toolAnnotation) {
		super();
		this.instance = this;
		this.method = method;
		this.toolAnnotation = toolAnnotation;
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
		
		JsonNode node = generateJsonSchema(method.getReturnType());
		try {
			JsonSchema schema = new ObjectMapper().readValue(node.toString(), JsonSchema.class);
			System.out.println(schema);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		System.out.println(generateJsonSchema(method.getReturnType()).toString());
		return generateJsonSchema(method.getReturnType()).toString();
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
				.outputSchema(createOutputSchema())
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