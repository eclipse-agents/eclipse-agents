package org.eclipse.mcp.factory;

import java.util.Arrays;
import java.util.List;

import com.github.victools.jsonschema.module.jackson.JacksonOption;

/**
 * Interface for components contributing MCP functionality
 */
public interface IFactory {
	/**
	 * @return Unique identifier used to track enablement preferences. Default to class cannonical name
	 */
	public default String getId() {
		return getClass().getCanonicalName();
	}
	
	public default List<JacksonOption> getJsonSchemaJacksonOptions() {
		return Arrays.asList(
			JacksonOption.RESPECT_JSONPROPERTY_ORDER,
            JacksonOption.RESPECT_JSONPROPERTY_REQUIRED,
            JacksonOption.FLATTENED_ENUMS_FROM_JSONVALUE);
//            JacksonOption.FLATTENED_ENUMS_FROM_JSONPROPERTY,
//            JacksonOption.INCLUDE_ONLY_JSONPROPERTY_ANNOTATED_METHODS,
//            JacksonOption.IGNORE_PROPERTY_NAMING_STRATEGY,
//            JacksonOption.ALWAYS_REF_SUBTYPES,
//            JacksonOption.INLINE_TRANSFORMED_SUBTYPES,
//            JacksonOption.SKIP_SUBTYPE_LOOKUP,
//            JacksonOption.IGNORE_TYPE_INFO_TRANSFORM,
//            JacksonOption.JSONIDENTITY_REFERENCE_ALWAYS_AS_ID);
	}
}


