/*******************************************************************************
 * Copyright (c) 2025 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.mcp.internal.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceManager;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;


public class AcpGeneralPreferencePage extends PreferencePage
		implements IPreferenceConstants, IWorkbenchPreferencePage, SelectionListener, ModifyListener {

	VerifyListener integerListener;
	PreferenceManager preferenceManager;
	
	Text node;
	Text gemini;
	
	public AcpGeneralPreferencePage() {
		super();

		integerListener = (VerifyEvent e) -> {
			String string = e.text;
			e.doit = string.matches("\\d*"); //$NON-NLS-1$
			return;
		};
	}

	@Override
	protected Control createContents(Composite ancestor) {

		Composite parent = new Composite(ancestor, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);

		
		Label instructions = new Label(parent, SWT.WRAP);
		instructions.setText("ACP lets you chat with CLI agents like Gemini and Claude Code");
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL);
		gd.widthHint = convertWidthInCharsToPixels(80);
		gd.horizontalSpan = 4;
		instructions.setLayoutData(gd);
		
		Label label = new Label(parent, SWT.NONE);
		label.setText("Node:");
		label.setLayoutData(new GridData());
		
		node = new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER);
		node.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		((GridData)node.getLayoutData()).horizontalSpan = 3;
		
		label = new Label(parent, SWT.NONE);
		label.setText("Gemini CLI:");
		label.setLayoutData(new GridData());
		
		gemini = new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER);
		gemini.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		((GridData)gemini.getLayoutData()).horizontalSpan = 3;
		
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent,
				"org.eclipse.agent.acp.preferences.AcpGeneralPreferencePage"); //$NON-NLS-1$

		loadPreferences();
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
		node.setText(store.getString(P_ACP_NODE));
		gemini.setText(store.getString(P_ACP_GEMINI));
	}

	private void savePreferences() {
		IPreferenceStore store = getPreferenceStore();

		store.setValue(P_ACP_NODE, node.getText());
		store.setValue(P_ACP_GEMINI, gemini.getText());;

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

		node.setText(store.getDefaultString(P_ACP_NODE));
		gemini.setText(store.getDefaultString(P_ACP_GEMINI));
		
		updateValidation();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		widgetSelected(event);
	}

	@Override
	public void widgetSelected(SelectionEvent event) {
		updateValidation();
	}

	@Override
	public void modifyText(ModifyEvent event) {
		updateValidation();
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}