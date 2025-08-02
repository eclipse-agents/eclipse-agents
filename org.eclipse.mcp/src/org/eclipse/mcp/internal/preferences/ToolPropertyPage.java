package org.eclipse.mcp.internal.preferences;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.util.BidiUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PropertyPage;

public class ToolPropertyPage extends PropertyPage implements IPreferenceConstants, IWorkbenchPreferencePage, SelectionListener, ModifyListener {

	private Text fNameText;
	private Text fDescriptionText;
	private Combo fContextCombo;
	private Button fInsertVariableButton;
	private Button fAutoInsertCheckbox;
	private boolean fIsNameModifiable;

		
	/**
	 * Creates a new dialog.
	 *
	 * @param parent the shell parent of the dialog
	 * @param template the template to edit
	 * @param edit whether this is a new template or an existing being edited
	 * @param isNameModifiable whether the name of the template may be modified
	 * @param registry the context type registry to use
	 */
	public ToolPropertyPage() {
		super();

		setTitle("Run SQL Options");

	}

	@Override
	protected Control createContents(Composite ancestor) {
	
		Composite parent= new Composite(ancestor, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		parent.setLayout(layout);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		
			createLabel(parent, "Name");

			Composite composite= new Composite(parent, SWT.NONE);
			composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			layout= new GridLayout();
			layout.numColumns= 4;
			layout.marginWidth= 0;
			layout.marginHeight= 0;
			composite.setLayout(layout);

			fNameText= createText(composite);
			fNameText.addModifyListener(this);
			fNameText.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {
				}

				@Override
				public void focusLost(FocusEvent e) {
	
				}
			});
			BidiUtils.applyBidiProcessing(fNameText, BidiUtils.BTD_DEFAULT);

			createLabel(composite, "Context");
			fContextCombo= new Combo(composite, SWT.READ_ONLY);


			fContextCombo.addModifyListener(this);
//			SWTUtil.setDefaultVisibleItemCount(fContextCombo);

			fAutoInsertCheckbox= createCheckbox(composite, "autoinsert");
			fAutoInsertCheckbox.setSelection(true);
		

		createLabel(parent, "description");

		int descFlags= fIsNameModifiable ? SWT.BORDER : SWT.BORDER;
		fDescriptionText= new Text(parent, descFlags );
		fDescriptionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		fDescriptionText.addModifyListener(this);
		BidiUtils.applyBidiProcessing(fDescriptionText, BidiUtils.BTD_DEFAULT);

		Label patternLabel= createLabel(parent, "Input Schema");
		patternLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		
		Text patternText = new Text(parent, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		patternText.setLayoutData(new GridData());
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 40;
		data.widthHint = 420;
		patternText.setLayoutData(data);
		patternText.setText("{\n"
				+ "  \"type\": \"object\",\n"
				+ "  \"properties\": {\n"
				+ "    \"query\": {\n"
				+ "      \"type\": \"string\"\n"
				+ "    }\n"
				+ "  },\n"
				+ "  \"required\": [\"query\"]\n"
				+ "}");
		
		return parent;
	}
	
	private static Label createLabel(Composite parent, String name) {
		Label label= new Label(parent, SWT.NULL);
		label.setText(name);
		label.setLayoutData(new GridData());

		return label;
	}

	private static Text createText(Composite parent) {
		Text text= new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return text;
	}

	private static Button createCheckbox(Composite parent, String name) {
		Button button= new Button(parent, SWT.CHECK);
		button.setText(name);
		button.setLayoutData(new GridData());

		return button;
	}
	
	@Override
	public void modifyText(ModifyEvent arg0) {
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
	public void init(IWorkbench arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void performApply() {
		
	}

	@Override
	public boolean performCancel() {
		return super.performCancel();
	}

	@Override
	public boolean performOk() {
		
		return super.performOk();
	}
    
    @Override
	protected void performDefaults() {
    	
	}
	

	
}
