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
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.mcp.Activator;
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

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			System.out.println(element);
			if (element instanceof IPreferencedServer) {
				switch (columnIndex) {
					case 0:
						return ((IPreferencedServer)element).getName();
					case 1:
						return ((IPreferencedServer)element).getDescription();
					case 2:
						return ((IPreferencedServer)element).getHttpPort();
					case 3:
						return "OK";
					default:
						return "e"; //$NON-NLS-1$
				}
			}
			return "?";
		}
	}
	
	private class ToolsLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			System.out.println(element);
			if (element instanceof Tool) {
				switch (columnIndex) {
					case 0:
						return ((Tool)element).getName();
					case 1:
						return "Tool";
					case 2:
						return ((Tool)element).getDescription();
					default:
						return "e"; //$NON-NLS-1$
				}
			} else if (element instanceof ResourceFactory) {
				switch (columnIndex) {
				case 0:
					return ((ResourceFactory)element).getName();
				case 1:
					return "Resource";
				case 2:
					return ((ResourceFactory)element).getDescription();
				default:
					return "e"; //$NON-NLS-1$
			}
		}
			return "?";
		}
	}


	@Override
	protected Control createContents(Composite ancestor) {

		Composite parent= new Composite(ancestor, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		parent.setLayout(layout);

		Composite innerParent= new Composite(parent, SWT.NONE);
		GridLayout innerLayout= new GridLayout();
		innerLayout.numColumns= 1;
		innerLayout.marginHeight= 0;
		innerLayout.marginWidth= 0;
		innerParent.setLayout(innerLayout);
		GridData gd= new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan= 2;
		innerParent.setLayoutData(gd);

		String[] columnNames = new String[] { 
			"Name", "Description", "HTTP Port", "Status"
		};
		
		serverComposite = new TableComposite(innerParent, columnNames, new ServersLabelProvider()) {		
			@Override
			public void doubleClick(DoubleClickEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				
				toolsComposite.getTableViewer().setInput(arg0.getStructuredSelection().getFirstElement());
			
				
			}

			@Override
			public void checkStateChanged(CheckStateChangedEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Object[] getElements(Object arg0) {
				return preferenceManager.getServers();
			}
			
		};

		GridData data= new GridData(GridData.FILL_BOTH);
		data.widthHint= 360;
		data.heightHint= convertHeightInCharsToPixels(15);
		serverComposite.setLayoutData(data);

		serverComposite.getTableViewer().setInput(preferenceManager);
		serverComposite.getTableViewer().setAllChecked(false);
//		TODO tableComposite.getTableViewer().setCheckedElements();

		// TOOLS
		columnNames = new String[] { 
			"Name", "Type", "Description"
		};
			
		toolsComposite = new TableComposite(innerParent, columnNames, new ToolsLabelProvider()) {		
			@Override
			public void doubleClick(DoubleClickEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void checkStateChanged(CheckStateChangedEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Object[] getElements(Object arg0) {
				if (arg0 instanceof IPreferencedServer) {
					return Stream.concat(
						Arrays.stream(((IPreferencedServer)arg0).getTools()), 
						Arrays.stream(((IPreferencedServer)arg0).getResourceFactories()))
                    	.toArray();
				}
				return new Object[0];
			}
			
		};

		data= new GridData(GridData.FILL_BOTH);
		data.widthHint= 360;
		data.heightHint= convertHeightInCharsToPixels(25);
		toolsComposite.setLayoutData(data);

		toolsComposite.getTableViewer().setInput(preferenceManager);
		toolsComposite.getTableViewer().setAllChecked(false);
		// --END TOOLS
		
		
//		TODO updateButtons();
		Dialog.applyDialogFont(parent);
		innerParent.layout();
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "org.eclipse.mcp.internal.preferences.McpGeneralPreferencePage"); //$NON-NLS-1$		
		
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
	public void modifyText(ModifyEvent arg0) {
		updateValidation();
	}
}