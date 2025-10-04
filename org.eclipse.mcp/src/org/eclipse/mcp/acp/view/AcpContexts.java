package org.eclipse.mcp.acp.view;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mcp.Activator;
import org.eclipse.mcp.acp.protocol.AcpSchema.Annotations;
import org.eclipse.mcp.acp.protocol.AcpSchema.ContentBlock;
import org.eclipse.mcp.acp.protocol.AcpSchema.EmbeddedResourceBlock;
import org.eclipse.mcp.acp.protocol.AcpSchema.ResourceLinkBlock;
import org.eclipse.mcp.acp.protocol.AcpSchema.Role;
import org.eclipse.mcp.acp.protocol.AcpSchema.TextResourceContents;
import org.eclipse.mcp.platform.resource.WorkspaceResourceAdapter;
import org.eclipse.mcp.resource.IResourceTemplate;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import io.modelcontextprotocol.spec.McpSchema.ResourceLink;

public class AcpContexts extends Composite {

	Map<String, ContentBlock> contexts;
	Map<String, Chips> chips;
	
	public AcpContexts(Composite parent, int style) {
		super(parent, style);
		setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		setLayout(new GridLayout(3, false));
		
		contexts = new HashMap<String, ContentBlock>();
		chips = new HashMap<String, Chips>();
	}
	
	public void addEmbeddedResourceContext(String name, String uri) {
		if (!contexts.containsKey(uri)) {
			IResourceTemplate<?, ?> resourceTemplate = Activator.getDefault().getServerManager().getResourceTemplate(uri);
			if (resourceTemplate != null) {
				ResourceLink link = resourceTemplate.toResourceLink();
				TextResourceContents contents = new TextResourceContents(
						null, 
						link.mimeType(),
						resourceTemplate.toContent(),
						uri);
				EmbeddedResourceBlock block = new EmbeddedResourceBlock(null, null, contents, "resource");
				contexts.put(uri, block);
				
				Chips chip = new Chips(this, SWT.CLOSE);
				chip.setText(name);
				chip.addCloseListener(e->{
					contexts.remove(uri);
					chip.dispose();
				});
				chips.put(uri, chip);
				
				getParent().getParent().layout(true, true);
			}
		}
	}
	
	public void addLinkedResourceContext(String name, String uri) {
		if (!contexts.containsKey(uri)) {
			WorkspaceResourceAdapter resourceAdapter = new WorkspaceResourceAdapter(uri);
			
			ResourceLink link = resourceAdapter.toResourceLink();

			List<Role> roles = new ArrayList<Role>();
			Double priority = null;
			Integer size = null;
			
			if (link.size() != null) {
				size = link.size().intValue();
			}
			if (link.annotations() != null) {
				for (int i =0; i<link.annotations().audience().size(); i++) {
					roles.add(Role.valueOf(link.annotations().audience().get(i).name()));
				}
				priority = link.annotations().priority();
			} else {
				roles.add(Role.assistant);
				roles.add(Role.user);
			}
			
			long timestampMillis = resourceAdapter.getModel().getModificationStamp();
			Instant instant = Instant.ofEpochMilli(timestampMillis);
			String lastModified = DateTimeFormatter.ISO_INSTANT.format(instant);
			
			ResourceLinkBlock block = new ResourceLinkBlock(
					null,
					new Annotations(null, roles.toArray(Role[]::new), lastModified, priority),
					link.description(),
					link.mimeType(),
					link.name(),
					size,
					link.title(),
					"resource_link",
					link.uri());
			
			
			contexts.put(uri, block);
			
			Chips chip = new Chips(this, SWT.CLOSE);
			chip.setText(name);
			chip.addCloseListener(e->{
				contexts.remove(uri);
				chip.dispose();
			});
			chips.put(uri, chip);
			
			getParent().getParent().layout(true, true);
		}
	}
	
	public Collection<ContentBlock> getContextBlocks() {
		return contexts.values();
	}
	
	public void clearAcpContexts() {
		for (Chips chip: chips.values()) {
			if (!chip.isDisposed()) {
				chip.dispose();
			}
		}
		chips.clear();
		contexts.clear();
		getParent().getParent().layout(true, true);
	}
	
}
