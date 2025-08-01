package org.eclipse.mcp.internal.preferences;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.util.BidiUtils;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public abstract class TableComposite extends Composite implements IStructuredContentProvider, IDoubleClickListener, ISelectionChangedListener, ICheckStateListener, SelectionListener {

	private CheckboxTableViewer viewer;
	Button add;
	Button edit;
	Button remove;
	Button restore;
	Button revert;
	
	public TableComposite(Composite parent, String[] columnNames, ITableLabelProvider labelProvider) {
		
		super(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.numColumns= 2;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		setLayout(layout);
		
		GridData gd= new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan= 2;
		setLayoutData(gd);
		
		Composite tableComposite = new Composite(this, SWT.NONE);
		GridData data= new GridData(GridData.FILL_BOTH);
		data.widthHint= 360;
		data.heightHint= 20;
		tableComposite.setLayoutData(data);

		data= new GridData(GridData.FILL_BOTH);
		data.widthHint= 360;
		data.heightHint = 20;
		tableComposite.setLayoutData(data);
	
		TableColumnLayout columnLayout= new TableColumnLayout();
		tableComposite.setLayout(columnLayout);
		Table table= new Table(tableComposite, SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
	
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableViewerComparator comparator= new TableViewerComparator();
		
		for (int i = 0; i < columnNames.length; i++) {	
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(columnNames[i]);
			columnLayout.setColumnData(column, new ColumnWeightData(columnNames.length - i));
			column.addSelectionListener(new TableColumnSelectionAdapter(column, i, comparator));
		}
	
		viewer = new CheckboxTableViewer(table);
		viewer.setLabelProvider(labelProvider);
		viewer.setContentProvider(this);
		viewer.setComparator(comparator);
	
		table.setSortColumn(table.getColumn(0));
		table.setSortDirection(comparator.getDirection());
	
		viewer.addDoubleClickListener(this);
	
		viewer.addSelectionChangedListener(this);
	
		viewer.addCheckStateListener(this);
	
		BidiUtils.applyTextDirection(viewer.getControl(), BidiUtils.BTD_DEFAULT);
	
		Composite buttons= new Composite(this, SWT.NONE);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		buttons.setLayout(layout);
	
		add = new Button(buttons, SWT.PUSH);
		add.setText("New");
		add.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		add.addSelectionListener(this);
	
		edit = new Button(buttons, SWT.PUSH);
		edit.setText("Edit");
		edit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		edit.addSelectionListener(this);
	
		remove = new Button(buttons, SWT.PUSH);
		remove.setText("Remove");
		remove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		remove.addSelectionListener(this);
	
		createSeparator(buttons);
	
		restore = new Button(buttons, SWT.PUSH);
		restore.setText("Restore Removed");
		restore.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		restore.addSelectionListener(this);
	
		revert = new Button(buttons, SWT.PUSH);
		revert.setText("Revert");
		revert.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		revert.addSelectionListener(this);
	

	}
	

	
	private Label createSeparator(Composite parent) {
		Label separator= new Label(parent, SWT.NONE);
		separator.setVisible(false);
		GridData gd= new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.verticalAlignment= GridData.BEGINNING;
		gd.heightHint= 4;
		separator.setLayoutData(gd);
		return separator;
	}

	protected Object getSelection() {
		if (getTableViewer().getStructuredSelection() != null) {
			return getTableViewer().getStructuredSelection().getFirstElement();
		}
		return null;
	}
	protected CheckboxTableViewer getTableViewer() {
		return viewer;
	}

	private final class TableViewerComparator extends ViewerComparator {

		private int column;
		private boolean asc;

		public TableViewerComparator() {
			column = 0;
			asc = true;
		}

		public int getDirection() {
			return asc ? SWT.DOWN : SWT.UP;
		}

		public void setColumn(int column) {
			if (this.column == column) {
				asc = !asc;
			} else {
				this.column = column;
				asc = true;
			}
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {

			if (viewer instanceof TableViewer) {
				ITableLabelProvider provider = (ITableLabelProvider)((TableViewer)viewer).getLabelProvider();

				String left = provider.getColumnText(e1, column);
				String right = provider.getColumnText(e2, column);
				
				return (asc) ? getComparator().compare(left, right) : getComparator().compare(right, left);
			}

			return super.compare(viewer, e1, e2);
		}
	}

	private final class TableColumnSelectionAdapter extends SelectionAdapter {

		private final TableColumn column;

		private final int columnIndex;

		private final TableViewerComparator comparator;

		public TableColumnSelectionAdapter(TableColumn column, int index, TableViewerComparator comparator) {
			this.column = column;
			this.columnIndex = index;
			this.comparator = comparator;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			comparator.setColumn(columnIndex);
			viewer.getTable().setSortDirection(comparator.getDirection());
			viewer.getTable().setSortColumn(column);
			viewer.refresh();
		}
	}
}
