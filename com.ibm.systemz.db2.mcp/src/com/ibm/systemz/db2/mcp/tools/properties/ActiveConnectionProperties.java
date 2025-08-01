/*******************************************************************************
 * IBM Confidential - OCO Source Materials
 * 
 * 5724-T07 IBM Rational Developer for System z Copyright IBM Corporation 2022, 2023
 * 
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of what
 * has been deposited with the U.S. Copyright Office.
 *******************************************************************************/

package com.ibm.systemz.db2.mcp.tools.properties;


import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;


public class ActiveConnectionProperties  extends PropertyPage implements IWorkbenchPreferencePage, SelectionListener, ModifyListener {

	


	private Composite control;
	
	public ActiveConnectionProperties() {
		super();
		setTitle("Db2 for z/OS");
	}
	
	@Override
	protected Control createContents(Composite parent) {
		
		control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(2, false));
		control.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, true));

		Button rollback = new Button(control, SWT.CHECK);
		rollback.setText("Only allow read-only queries");
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		rollback.setLayoutData(gd);
		
		Button connectionDeferToEditor = new Button(control, SWT.CHECK);
		connectionDeferToEditor.setText("Use connection associated with Active Editor when available");
		gd = new GridData();
		gd.horizontalSpan = 2;
		connectionDeferToEditor.setLayoutData(gd);
		
		Button runOptionsDeferToEditor = new Button(control, SWT.CHECK);
		runOptionsDeferToEditor.setText("Use Run SQL Options of the Active Editor when available");
		gd = new GridData();
		gd.horizontalSpan = 2;
		runOptionsDeferToEditor.setLayoutData(gd);
		
		Button connectionFixed = new Button(control, SWT.CHECK);
		connectionFixed.setText("Always use the following connection:");
		gd = new GridData();
		gd.horizontalSpan = 2;
		connectionFixed.setLayoutData(gd);
		
		Combo connectionCombo = new Combo(control, SWT.READ_ONLY);
		connectionCombo.setText("Select one...");
		gd = new GridData();
		gd.horizontalSpan = 2;
		connectionFixed.setLayoutData(gd);
		
		control.setSize(control.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "com.ibm.systemz.db2.ide.preferences.Db2RunSqlOptionsPropertyPage"); //$NON-NLS-1$

		loadPreferences();
		updateValidation();
		updateEnablement();
		
		IAdaptable element = getElement();

		return control;
	}

	@Override
	public void init(IWorkbench workbench) {
		
	}
	
	private void updateValidation() {
		
	}
	
	private void updateEnablement() {

	}

	private void loadPreferences() {
		IAdaptable element = getElement();
	}
	
	private void loadModelIntoUX(DialogSettings model) {
		
	}
	
	@Override
	protected void performApply() {
		savePreferences();
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
    	
    	loadModelIntoUX(null);
		
		updateEnablement();
		updateValidation();
	}
    
	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		widgetSelected(event);		
	}

	@Override
	public void widgetSelected(SelectionEvent event) {
		updateEnablement();
		updateValidation();
	}

	@Override
	public void modifyText(ModifyEvent arg0) {
		updateValidation();
	}
	
	private void savePreferences() {
		IAdaptable element = getElement();
		
	}
	
	private void updateSelectonIfNeeded(Button button, boolean select) {
		if (button.getSelection() != select) {
			button.setSelection(select);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}

}
