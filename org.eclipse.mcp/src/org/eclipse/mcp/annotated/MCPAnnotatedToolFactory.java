package org.eclipse.mcp.annotated;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.mcp.IMCPFactory;
import org.eclipse.mcp.IMCPFactory.Tool;
import org.eclipse.mcp.IMCPFactory.ToolArg;
import org.eclipse.mcp.IMCPToolFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.modelcontextprotocol.spec.McpSchema.ToolAnnotations;



public class MCPAnnotatedToolFactory implements IMCPToolFactory {
	
	
	Object instance;
	Method method;
	Tool toolAnnotation;
	String inputSchema;
	String outputSchema;
	

	public MCPAnnotatedToolFactory() {}

	public MCPAnnotatedToolFactory(IMCPFactory instance, Method method, Tool toolAnnotation) {
		super();
		this.instance = instance;
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

	public String getInputSchema() {
		return inputSchema.toString();
	}

	@Override
	public String getCategory() {
		return toolAnnotation.category();
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
			inputs.add(args.get(paramName));
		}
		Object result = null;
		try {
			result = method.invoke(this.instance, inputs.toArray());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		if (result instanceof String[]) {
			return (String[])result;
		}
		return null;
	}
}