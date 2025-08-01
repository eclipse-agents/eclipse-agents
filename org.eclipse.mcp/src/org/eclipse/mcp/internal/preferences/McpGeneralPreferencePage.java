/*******************************************************************************
 * IBM Confidential - OCO Source Materials
 * 
 * 5724-T07 IBM Rational Developer for System z Copyright IBM Corporation 2022, 2024
 * 
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of what
 * has been deposited with the U.S. Copyright Office.
 *******************************************************************************/
package org.eclipse.mcp.internal.preferences;

import java.util.Arrays;
import java.util.stream.Stream;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.mcp.Activator;
import org.eclipse.mcp.IMCPElementPropertyInput;
import org.eclipse.mcp.Images;
import org.eclipse.mcp.internal.ExtensionManager.ResourceFactory;
import org.eclipse.mcp.internal.ExtensionManager.Tool;
import org.eclipse.mcp.internal.PreferenceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class McpGeneralPreferencePage extends PreferencePage
		implements IPreferenceConstants, IWorkbenchPreferencePage, SelectionListener, ModifyListener {

	VerifyListener integerListener;
	PreferenceManager preferenceManager;

	TableComposite serverComposite, toolsComposite;

	public McpGeneralPreferencePage() {
		super();

		preferenceManager = new PreferenceManager();
		preferenceManager.load();

		integerListener = (VerifyEvent e) -> {
			String string = e.text;
			e.doit = string.matches("\\d*"); //$NON-NLS-1$
			return;
		};
	}

	private class ServersLabelProvider extends LabelProvider implements ITableLabelProvider {

		public final static String[] columns = { "Name", "Description", "HTTP Port" };

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 0) {
				return Activator.getDefault().getImageRegistry().get(Images.IMG_SERVER);
			}
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			System.out.println(element);
			if (element instanceof IPreferencedServer) {
				switch (columnIndex) {
				case 0:
					return ((IPreferencedServer) element).getName();
				case 1:
					return ((IPreferencedServer) element).getDescription();
				case 2:
					return ((IPreferencedServer) element).getHttpPort();
				default:
					return "e"; //$NON-NLS-1$
				}
			}
			return "?";
		}
	}

	private class ToolsLabelProvider extends LabelProvider implements ITableLabelProvider {

		public final static String[] columns = new String[] { "Name", "Type", "Category", "Description" };

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 0) {
				if (element instanceof Tool) {
					return Activator.getDefault().getImageRegistry().get(Images.IMG_TOOL);
				} else if (element instanceof ResourceFactory) {
					return Activator.getDefault().getImageRegistry().get(Images.IMG_RESOURCEMANAGER);
				}
			}
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof ServerElement) {
				switch (columnIndex) {
				case 0:
					return ((ServerElement) element).getName();
				case 1:
					return (element instanceof Tool) ? "Tool" : "Resources";
				case 2:
					return ((ServerElement) element).getCategory();
				case 3:
					return ((ServerElement) element).getDescription();
				default:
					return "e"; //$NON-NLS-1$
				}
			}
			return "?";
		}
	}

	@Override
	protected Control createContents(Composite ancestor) {

		Composite parent = new Composite(ancestor, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);

		Composite innerParent = new Composite(parent, SWT.NONE);
		GridLayout innerLayout = new GridLayout();
		innerLayout.numColumns = 1;
		innerLayout.marginHeight = 0;
		innerLayout.marginWidth = 0;
		innerParent.setLayout(innerLayout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		innerParent.setLayoutData(gd);

		serverComposite = new TableComposite(innerParent, ServersLabelProvider.columns, new ServersLabelProvider()) {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				toolsComposite.getTableViewer().setInput(event.getStructuredSelection().getFirstElement());

			}

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public Object[] getElements(Object parent) {
				return preferenceManager.getServers();
			}

		};

		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 360;
		data.heightHint = convertHeightInCharsToPixels(15);
		serverComposite.setLayoutData(data);

		serverComposite.getTableViewer().setInput(preferenceManager);
		serverComposite.getTableViewer().setAllChecked(false);
//		TODO tableComposite.getTableViewer().setCheckedElements();

		toolsComposite = new TableComposite(innerParent, ToolsLabelProvider.columns, new ToolsLabelProvider()) {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				if (event.getSource() == toolsComposite.edit) {
					
					Object serverSelection = serverComposite.getSelection();
					Object elementSelection = toolsComposite.getSelection();
					
					if (serverSelection != null && elementSelection != null) {
						IPreferencedServer server = (IPreferencedServer)serverSelection;
						ServerElement element = (ServerElement)elementSelection;
						
						IMCPElementPropertyInput input = preferenceManager.getElementPropertyInput(server.getId(), element.getId());
							
						PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(
									Activator.getDisplay().getActiveShell(), input,
									"org.eclipse.mcp.internal.preferences.ToolPropertyPage",
									Stream.concat(Arrays.stream(new String[] { "org.eclipse.mcp.internal.preferences.ToolPropertyPage" }),
											Arrays.stream( element.getPropertyEditorIds()))
											.toArray(String[]::new),
									input);
						
						if (dialog != null) {
							dialog.open();
						}
					}
				}
			}

			@Override
			public Object[] getElements(Object parent) {
				if (parent instanceof IPreferencedServer) {
					return Stream.concat(Arrays.stream(((IPreferencedServer) parent).getTools()),
							Arrays.stream(((IPreferencedServer) parent).getResourceFactories())).toArray();
				}
				return new Object[0];
			}

		};

		data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 360;
		data.heightHint = convertHeightInCharsToPixels(25);
		toolsComposite.setLayoutData(data);

		toolsComposite.getTableViewer().setInput(preferenceManager);
		toolsComposite.getTableViewer().setAllChecked(false);
		// --END TOOLS

//		TODO updateButtons();
		Dialog.applyDialogFont(parent);
		innerParent.layout();

		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent,
				"org.eclipse.mcp.internal.preferences.McpGeneralPreferencePage"); //$NON-NLS-1$

		updateValidation();

		return parent;
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	private void updateValidation() {
		String errorMessage = null;

		setValid(errorMessage == null);
		setErrorMessage(errorMessage);

	}

	private void loadPreferences() {
		IPreferenceStore store = getPreferenceStore();
	}

	private void savePreferences() {
		IPreferenceStore store = getPreferenceStore();
	}

	@Override
	public boolean performCancel() {
		return super.performCancel();
	}

	@Override
	public boolean performOk() {
		savePreferences();
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		IPreferenceStore store = getPreferenceStore();

		updateValidation();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		widgetSelected(event);
	}

	@Override
	public void widgetSelected(SelectionEvent event) {

	}

	@Override
	public void modifyText(ModifyEvent event) {
		updateValidation();
	}
}