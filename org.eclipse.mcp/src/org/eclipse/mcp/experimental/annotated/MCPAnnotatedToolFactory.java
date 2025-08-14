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
import java.util.List;
import java.util.Map;

import org.eclipse.mcp.MCPException;
import org.eclipse.mcp.factory.IToolFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.modelcontextprotocol.spec.McpSchema.ToolAnnotations;



public class MCPAnnotatedToolFactory implements IToolFactory {
	
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
	
	public static IToolFactory[] createToolFactories(Class<?> c) throws MCPException {
		List<IToolFactory> tools = new ArrayList<IToolFactory>();
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
		return tools.toArray(new IToolFactory[0]);
	}

	Object instance;
	Method method;
	Tool toolAnnotation;
	String inputSchema;
	String outputSchema;

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

		JsonObject result = new JsonObject();
		JsonObject properties = new JsonObject();
		JsonArray required = new JsonArray();
		
		result.addProperty("type", "object");
		result.add("properties", properties);
		result.add("required",  required);

		for (Parameter parameter: method.getParameters()) {
			ToolArg arg = parameter.getAnnotation(ToolArg.class);
			if (arg != null) {
				String name = arg.name();
				String desc = arg.description();
				boolean req = arg.required();

				String cannonicalType = parameter.getType().getCanonicalName();
				if (cannonicalType.endsWith("[][]")) {
					throw new IllegalArgumentException("Only 1-d arrays are supported: " + parameter.getType().getCanonicalName());
				}

				boolean isArray = cannonicalType.endsWith("[]");
				if (isArray) {
					cannonicalType = cannonicalType.substring(0, cannonicalType.length() - 2);
				}
				String jsonType = getJsonSchemaType(cannonicalType);

				if (name == null) {
					name = parameter.getName();
				}

				JsonObject attributes = new JsonObject();
				
				if (isArray) {
					attributes.addProperty("type", "array");
					JsonObject items = new JsonObject();
					items.addProperty("type", jsonType);
					attributes.add("items",  items);
				} else {
					attributes.addProperty("type", jsonType);
				}
				
				if (desc != null) {
					attributes.addProperty("description", desc);
				}
				
				properties.add(name, attributes);
				
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
		
		JsonObject result = new JsonObject();
		result.addProperty("type", "array");
		JsonObject items = new JsonObject();
		items.addProperty("type", "string");
		result.add("items",  items);
		
		return result.toString();
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
	
	@Override
	public String getCategory() {
		return toolAnnotation.category();
	}

	public String getInputSchema() {
		return inputSchema.toString();
	}
	
	public boolean isValid() {
		return true;
	}
	
	public String  getJsonSchemaType(String javaType) throws IllegalArgumentException {
		switch (javaType) {
		case "java.lang.Boolean":
		case "boolean":
			return "boolean";
		case 	"java.lang.Character":	
		case	"char":
		case 	"java.lang.String":
			return "string";
		case 	"java.lang.Double":
		case	"double":
		case 	"java.lang.Float":
		case	"float":
			return "number";
		case 	"java.lang.Integer":
		case	"int":
		case 	"java.lang.Long":
		case	"long":
		case 	"java.lang.Short":
		case	"short":
			return "integer";
		default:
			throw new IllegalArgumentException("Unexpected value: " + javaType);
		}
	}

	@Override
	public io.modelcontextprotocol.spec.McpSchema.Tool createTool() {
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
			
			String cannonicalType = param.getType().getCanonicalName();
			if (cannonicalType.endsWith("[][]")) {
				throw new IllegalArgumentException("Only 1-d arrays are supported: " + param.getType().getCanonicalName());
			}

			Object casted = cast(args.get(paramName), cannonicalType);
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
	
	public Object cast(Object value, String javaType) {
		switch (javaType) {
		case 	"java.lang.Character":	
		case	"char":
			return (char)value.toString().charAt(0);
		case 	"java.lang.Float":
		case	"float":
			return Float.valueOf(value.toString());
		case 	"java.lang.Long":
		case	"long":
			return Long.valueOf(value.toString());
		case 	"java.lang.Short":
		case	"short":
			return Short.valueOf(value.toString());
		case 	"java.lang.String[]":
			if (value instanceof List) {
				return ((List<?>)value).toArray(new String[0]);
			}
		case	"java.lang.Integer[]":
		case	"int[]":
			if (value instanceof List) {
				return ((List<?>)value).toArray(new Integer[0]);
			}
		
		default:
			return value;
		}
	}
}