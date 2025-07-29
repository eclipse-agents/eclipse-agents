/*******************************************************************************
 * IBM Confidential - OCO Source Materials
 * 
 * 5724-T07 IBM Rational Developer for System z Copyright IBM Corporation 2022, 2024
 * 
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of what
 * has been deposited with the U.S. Copyright Office.
 *******************************************************************************/
package org.eclipse.mcp.internal.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mcp.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class McpGeneralPreferencePage extends PreferencePage
		implements IPreferenceConstants, IWorkbenchPreferencePage, SelectionListener, ModifyListener {

	
	VerifyListener integerListener;


	public McpGeneralPreferencePage() {
		super();

		integerListener = (VerifyEvent e) -> {
			String string = e.text;
			e.doit = string.matches("\\d*"); //$NON-NLS-1$
			return;
		};
	}

	@Override
	protected Control createContents(Composite parent) {

		Composite middle = new Composite(parent, SWT.NONE);
		middle.setLayout(new GridLayout(1, true));
		middle.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Link link = new Link(middle, SWT.NONE);
		link.setText("");
		link.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				PreferencesUtil.createPreferenceDialogOn(parent.getShell(), "org.eclipse.sdk.capabilities", //$NON-NLS-1$
						null, null);
			}
		});
		link.setLayoutData(new GridData());
	

		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "com.ibm.systemz.db2.ide.preferences.Db2GeneralPreferencePage"); //$NON-NLS-1$
		loadPreferences();
		updateValidation();
		middle.pack(true);
		return middle;
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