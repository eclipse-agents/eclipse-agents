package org.eclipse.mcp.builtin.resource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.Annotations;
import io.modelcontextprotocol.spec.McpSchema.ResourceLink;
import io.modelcontextprotocol.spec.McpSchema.Role;

public class AbsoluteFileAdapter implements IResourceAdapter<File> {

	final String template = "file:///{absolute-path}";

	@Override
	public String getTemplate() {
		return template;
	}

	@Override
	public String getUniqueTemplatePrefix() {
		return template.substring(0, template.indexOf("{"));
	}

	@Override
	public File uriToEclipseObject(String uri) {
		File file = null;
		try {
			file = new File(new URI(uri));
			if (!file.exists()) {
				file = new File(new URI(URLDecoder.decode(uri)));
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return file;
	}

	@Override
	public Object eclipseObjectToJsonObject(File object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceLink eclipseObjectToResourceLink(File file) {

		McpSchema.ResourceLink.Builder builder =  McpSchema.ResourceLink.builder()
				.uri(eclipseObjectToURI(file))
				.name(file.getName())
				.description("Content of an file in an Eclipse workspace")
				.mimeType("text/plain");
		
		addAnnotations(builder);
		
		if (file.isFile()) {
			builder.size(file.length());
		}
		
		return builder.build();

	}

	@Override
	public String eclipseObjectToURI(File object) {
		if (object instanceof File) {
			return ((File)object).toURI().toString();
		}
		return null;
	}

	@Override
	public String eclipseObjectToResourceContent(File file) {
		if (file.isFile()) {
			try {
				return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			System.err.println("TODO its a folder");
		}

		return null;
	}
}
