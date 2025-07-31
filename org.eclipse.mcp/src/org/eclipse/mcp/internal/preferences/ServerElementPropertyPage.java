
/*******************************************************************************
 * IBM Confidential - OCO Source Materials
 * 
 * 5724-T07 IBM Rational Developer for System z Copyright IBM Corporation 2022, 2023
 * 
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of what
 * has been deposited with the U.S. Copyright Office.
 *******************************************************************************/
package org.eclipse.mcp.internal.preferences;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.accessibility.AccessibleListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;

import com.ibm.systemz.common.jface.Copyright;
import com.ibm.systemz.db2.Activator;
import com.ibm.systemz.db2.Messages;
import com.ibm.systemz.db2.ide.preferences.model.RunningOptions;
import com.ibm.systemz.db2.ide.validation.ImplicitRegisters;

public class ServerElementPropertyPage  extends PropertyPage implements IPreferenceConstants, IPreferenceListener, IWorkbenchPreferencePage, SelectionListener, ModifyListener {

	public static final String copyright = Copyright.COPYRIGHT;


	private Composite control;
	
	public ServerElementPropertyPage() {
		super();
		setTitle("General");
	}
	
	@Override
	protected Control createContents(Composite parent) {
		
		control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(1, false));
		control.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, true));

		Group connectionReuseGroup = new Group(control, SWT.NONE);
		connectionReuseGroup.setText("Group 1");
		connectionReuseGroup.setLayoutData(new GridData());
		connectionReuseGroup.setLayout(new GridLayout(1, true));
		
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
	
	private void loadModelIntoUX(RunningOptions model) {
		
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
    	model.loadFromDefaults();
    	loadModelIntoUX(model);
		
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
		IFile file = ((IFileEditorInput)element).getFile();
		RunningOptions model = new RunningOptions(file);
		loadUXIntoModel(model);
		model.saveToFile();
	}
	
	private void updateSelectonIfNeeded(Button button, boolean select) {
		if (button.getSelection() != select) {
			button.setSelection(select);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		EditorHelper.removePreferenceListener(this);
	}

	@Override
	public void preferenceChanged(IFile file, Set<QualifiedName> modifiedPreferenceKeys) {
		setJdbcPropertyMessages(file);
	}
	 
	@Override
	public Collection<QualifiedName> getFilteredQualifiedNames() {
		return PREFERENCE_FILTER;
	}
	
	/**
	 * If the active connection has JDBC properties set that affect the default
	 * current path or schema, display them in the background of the relevant Text 
	 * @param file
	 */
	private void setJdbcPropertyMessages(IFile file) {

		implicitRegisters = new ImplicitRegisters(file);

		Activator.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (schemaText.isDisposed() || pathText.isDisposed()) {
					return;
				}
				
				String implicitSchema = implicitRegisters.getImplicitSchema();
				if (implicitSchema == null) {
					schemaText.setMessage(""); //$NON-NLS-1$
				} else {
					schemaText.setMessage(implicitSchema);
				}
			
				String tooltip = implicitRegisters.getRunSqlOptionCurrentSchemaToolTip();
				if (tooltip == null) {
					schemaText.setToolTipText(""); //$NON-NLS-1$
				} else {
					schemaText.setToolTipText(tooltip);
				}
				
				pathText.setMessage(""); //$NON-NLS-1$
				
			}
		});
	}

}