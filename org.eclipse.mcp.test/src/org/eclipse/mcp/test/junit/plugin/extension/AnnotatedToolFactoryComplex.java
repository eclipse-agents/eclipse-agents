package org.eclipse.mcp.test.junit.plugin.extension;

import java.lang.reflect.Method;

import org.eclipse.mcp.experimental.annotated.MCPAnnotatedToolFactory;
import org.eclipse.mcp.test.junit.plugin.extension.json.Address;
import org.eclipse.mcp.test.junit.plugin.extension.json.Person;

/**
 * Example input:
{
    PERSON:
{
  "name": "Jeremy",
  "address": {
    "street": [
      "100 Main Street",
      "PO Box 123"
    ],
    "city": "Miami"
  },
  "parents": [
    {
      "name": "Ken"
    }
  ]
}

    , address:

{
  "street": [
    "100 Main Street",
    "PO Box 123"
  ],
  "city": "Miami"
}

} 

 */
public class AnnotatedToolFactoryComplex extends MCPAnnotatedToolFactory {

	public AnnotatedToolFactoryComplex(Method method, Tool toolAnnotation) {
		super(method, toolAnnotation);
	}

	@Tool ( description = "Greets a complex user with a hello", 
			name = "test-hello-world-complex",
			title = "Test Hello World Complex")
	public String[] helloWorld(
			@ToolArg(name = "PERSON", description = "The person to say hello to")
			Person person,
			@ToolArg(name = "address", description = "Where the person is living")
			Address address
			) {
		
		String result = "Hello " + person.getName();
		
		if (person.getAddress() != null && person.getAddress().getCity() != null) {
			result += " from " + person.getAddress().getCity();
		}
		if (person.parents != null && person.parents.length > 0 && person.parents[0].getName() != null) {
			result += " son of " + person.parents[0].getName();
		}
		return new String[] {
			result
		};
	}
}
