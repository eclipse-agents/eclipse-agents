package org.eclipse.mcp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


/**
 * Notes: do not use primitive types for non-required parameters as the null value will fail conversion
 * 
 * Support for case Boolean, Character,String, Double, Float, Integer, Long, Short and 1-d arrays of these
 */
public interface IMCPToolFactory {


	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Tool {
	    String id();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	public @interface ToolArg {
	    String name();
	    String description();
	    boolean required() default true;
	}

	public interface IMCPTool {
		
		public String getId();
		public String getSchema();
		public String[] apply(Map<String, Object> args);
		public void setElementProperties(IElementProperties properties);
		
	}
	
	public default IMCPTool[] createTools() {
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
		return tools.toArray(IMCPTool[]::new);
	}
	
	public class AnnotatedMCPTool implements IMCPTool {

		Object instance;
		Method method;
		String id;
		JsonObject schema;
		
		public AnnotatedMCPTool(Object instance, Method method, Tool tool) {
			super();
			this.instance = instance;
			this.method = method;
			
			this.id = tool.id();
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
//						Class type = parameter.getType();
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

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getSchema() {
			return schema.toString();
		}



		@Override
		public void setElementProperties(IElementProperties properties) {
			// TODO Auto-generated method stub
			
		}
		
		boolean isValid() {
			//TODO
			return true;
		}

		@Override
		public String[] apply(Map<String, Object> args) {
			List<Object> inputs = new ArrayList<Object>();
			for (Parameter param: method.getParameters()) {
				if (param.getClass().isInstance(IElementProperties.class)) {
					System.out.println("is element properties");
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
}
