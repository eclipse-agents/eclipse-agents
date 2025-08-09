package org.eclipse.mcp.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.mcp.IElementProperties;
import org.eclipse.mcp.IMCPToolFactory.IMCPAnnotatedTool;
import org.eclipse.mcp.IMCPToolFactory.Tool;
import org.eclipse.mcp.IMCPToolFactory.ToolArg;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class AnnotatedMCPTool implements IMCPAnnotatedTool {

	Object instance;
	Method method;
	String id;
	String name;
	String description;
	String categoryId;
	String[] propertyPageIds;
	JsonObject schema;
	
	public AnnotatedMCPTool(Object instance, Method method, Tool tool) {
		super();
		this.instance = instance;
		this.method = method;
		
		this.id = tool.id();
		this.name = tool.name();
		
		this.description = tool.description();
		this.categoryId = tool.categoryId();
		this.propertyPageIds = tool.propertyPageIds();
		
		schema = new JsonObject();
		JsonObject properties = new JsonObject();
		JsonArray required = new JsonArray();
		
		schema.addProperty("type", "object");
		schema.add("properties", properties);
		schema.add("required",  required);
		
		if (tool != null) {
			for (Parameter parameter: method.getParameters()) {
				ToolArg arg = parameter.getAnnotation(ToolArg.class);
				if (arg != null) {
					String name = arg.name();
					String desc = arg.description();
					boolean req = arg.required();
//					Class type = parameter.getType();
					String type = "string";
					
					System.out.println(parameter.getType().getCanonicalName());

					String pType = parameter.getType().getCanonicalName();
					if (pType.endsWith("[][]")) {
						throw new IllegalArgumentException("Only 1-d arrays are supported: " + parameter.getType().getCanonicalName());
					}

					boolean isArray = pType.endsWith("[]");
					if (isArray) {
						pType = pType.substring(0, pType.length() - 2);
					}
					
					switch (pType) {
						case "java.lang.Boolean":
						case "boolean":
							type = "boolean";
							break;
						case 	"java.lang.Character":	
						case	"char":
						case 	"java.lang.String":
							type = "string";
							break;
						case 	"java.lang.Double":
						case	"double":
						case 	"java.lang.Float":
						case	"float":
							type = "number";
							break;
						case 	"java.lang.Integer":
						case	"int":
						case 	"java.lang.Long":
						case	"long":
						case 	"java.lang.Short":
						case	"short":
							type = "integer";
							break;
						default:
							throw new IllegalArgumentException("Unexpected value: " + parameter.getType().getCanonicalName());
					}

					if (name == null) {
						name = parameter.getName();
					}

					JsonObject attributes = new JsonObject();
					
					if (isArray) {
						attributes.addProperty("type", "array");
						JsonObject items = new JsonObject();
						items.addProperty("type", type);
						attributes.add("items",  items);
					} else {
						attributes.addProperty("type", type);
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
		}
	}

	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getInputSchema() {
		return schema.toString();
	}

	public String getCategoryId() {
		return categoryId;
	}

	public String[] getPropertyPageIds() {
		return propertyPageIds;
	}

	
	public boolean isValid() {
		//TODO
		return true;
	}

	@Override
	public String[] apply(Map<String, Object> args, IElementProperties properties) {
		List<Object> inputs = new ArrayList<Object>();
		for (Parameter param: method.getParameters()) {
			if (param.getClass().isInstance(IElementProperties.class)) {
				inputs.add(properties);
			} else {
				ToolArg arg = param.getAnnotation(ToolArg.class);
				String paramName = param.getName();
				if (arg != null && arg.name() != null) {
					paramName = arg.name();
				}
				inputs.add(args.get(paramName));
			}
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