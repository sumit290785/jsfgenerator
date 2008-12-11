package jsfgenerator.ui.composites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jsfgenerator.ui.model.EntityDescription;
import jsfgenerator.ui.model.EntityFieldDescription;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class EntityFieldSelectionComposite extends Composite {

	private static final String COLLECTION_FIELD = "Complex entity form...";

	protected class InputTagEditingSupport extends EditingSupport {

		private ComboBoxViewerCellEditor editor;

		public InputTagEditingSupport(ColumnViewer viewer) {
			super(viewer);
			editor = new ComboBoxViewerCellEditor(((TableViewer) viewer).getTable());
			editor.setContenProvider(new ArrayContentProvider());
			editor.setLabelProvider(new LabelProvider() {

				@Override
				public String getText(Object element) {
					return (String) element;
				}

			});
			;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {

			// refresh the drop down
			ArrayList<String> input = new ArrayList<String>();
			input.addAll(inputTagIds);
			if (((EntityFieldDescription) element).isCollectionOfEntity() && !selectedEntityDescription.isEmbedded()) {
				input.add(COLLECTION_FIELD);
			}
			editor.setInput(input);

			return editor;
		}

		@Override
		protected Object getValue(Object element) {
			EntityFieldDescription entityField = ((EntityFieldDescription) element);

			if (entityField.isCollectionInComplexForm()) {
				return COLLECTION_FIELD;
			} else {
				return entityField.getInputTagId();
			}
		}

		@Override
		protected void setValue(Object element, Object value) {

			EntityFieldDescription entityField = ((EntityFieldDescription) element);
			if (value != null && COLLECTION_FIELD.equals((String) value) && !selectedEntityDescription.isEmbedded()) {
				entityField.setCollectionInComplexForm(selectedEntityDescription.getNode());
				entityField.setInputTagId(null);
			} else {
				entityField.setCollectionInComplexForm(null);
				entityField.setInputTagId((String) value);
			}

			masterPart.refresh();
			getViewer().refresh();
		}

	}

	protected class MasterTreeContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {

			if (parentElement instanceof EntityDescription) {
				EntityDescription entityDescription = (EntityDescription) parentElement;
				List<EntityDescription> embeddedEntityDescription = new ArrayList<EntityDescription>();
				for (EntityFieldDescription entityField : entityDescription.getEntityFieldDescriptions()) {
					if (entityField.isCollectionInComplexForm()) {
						embeddedEntityDescription.add(entityField.getEntityDescription());
					}
				}

				return embeddedEntityDescription.toArray();
			}

			return null;
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof EntityDescription) {
				EntityDescription entityDescription = (EntityDescription) element;
				for (EntityFieldDescription entityField : entityDescription.getEntityFieldDescriptions()) {
					if (entityField.isCollectionInComplexForm()) {
						return true;
					}
				}
			}
			return false;
		}

		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Object[]) {
				return (Object[]) inputElement;
			}
			if (inputElement instanceof Collection) {
				return ((Collection) inputElement).toArray();
			}
			return new Object[0];
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	protected class FieldTableViewer extends TableViewer {

		public FieldTableViewer(Composite parent, int style) {
			super(parent, style);
			createControl();
		}

		private void createControl() {
			setContentProvider(new ArrayContentProvider());

			// field
			TableViewerColumn fieldNameColumn = new TableViewerColumn(this, SWT.NONE);
			fieldNameColumn.setLabelProvider(new ColumnLabelProvider() {

				public String getText(Object element) {
					return ((EntityFieldDescription) element).getFieldName();
				}

			});
			fieldNameColumn.getColumn().setText("Field");
			fieldNameColumn.getColumn().setWidth(100);

			// type
			TableViewerColumn typeColumn = new TableViewerColumn(this, SWT.NONE);
			typeColumn.setLabelProvider(new ColumnLabelProvider() {

				public String getText(Object element) {
					return ((EntityFieldDescription) element).getClassName();
				}

			});
			typeColumn.getColumn().setText("Type");
			typeColumn.getColumn().setWidth(100);

			// inputtag id
			TableViewerColumn inputTagColumn = new TableViewerColumn(this, SWT.NONE);
			inputTagColumn.setLabelProvider(new ColumnLabelProvider() {

				public String getText(Object element) {
					EntityFieldDescription entityField = (EntityFieldDescription) element;
					if (entityField.isCollectionInComplexForm()) {
						return COLLECTION_FIELD;
					} else {
						return ((EntityFieldDescription) element).getInputTagId();
					}
				}

			});
			inputTagColumn.getColumn().setText("Input tag id");
			inputTagColumn.getColumn().setWidth(100);
			inputTagColumn.setEditingSupport(new InputTagEditingSupport(this));
		}

	}

	protected TreeViewer masterPart;

	protected Composite detailsPart;

	private FieldTableViewer fieldTable;

	private List<String> inputTagIds;

	private EntityDescription selectedEntityDescription;

	public EntityFieldSelectionComposite(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void createControl() {
		GridLayout layout = new GridLayout(2, false);
		setLayout(layout);

		masterPart = new TreeViewer(this, SWT.BORDER);
		masterPart.setContentProvider(new MasterTreeContentProvider());
		final GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.FILL;
		gridData.widthHint = 150;
		masterPart.getControl().setLayoutData(gridData);

		detailsPart = new Composite(this, SWT.NONE);
		detailsPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		detailsPart.setLayout(new GridLayout(1, false));

		fieldTable = new FieldTableViewer(detailsPart, SWT.BORDER);
		fieldTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fieldTable.getTable().setLinesVisible(true);
		fieldTable.getTable().setHeaderVisible(true);

		final Composite fileNameComposite = new Composite(detailsPart, SWT.NONE);
		fileNameComposite.setLayout(new GridLayout(2, false));
		final Label viewIdLabel = new Label(fileNameComposite, SWT.NONE);
		viewIdLabel.setText("View id:");
		viewIdLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		final Text viewIdText = new Text(detailsPart, SWT.BORDER);
		viewIdText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		viewIdText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				selectedEntityDescription.setViewId(viewIdText.getText());
			}
		});

		masterPart.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof EntityDescription) {
					return ((EntityDescription) element).getEntityClassName();
				}

				return null;
			}

		});

		masterPart.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {

				if (event.getSelection() instanceof StructuredSelection) {
					EntityDescription selectedEntityDescription = (EntityDescription) ((StructuredSelection) event.getSelection())
							.getFirstElement();
					refreshFieldTable(selectedEntityDescription);
					viewIdText.setText(selectedEntityDescription.getViewId());
				}
			}
		});

	}

	public void setInput(List<EntityDescription> entityDescriptions) {
		masterPart.setInput(entityDescriptions);
		refreshFieldTable(null);
	}

	private void refreshFieldTable(EntityDescription entityDescription) {
		this.selectedEntityDescription = entityDescription;
		if (entityDescription != null) {
			fieldTable.setInput(entityDescription.getEntityFieldDescriptions());
		} else {
			fieldTable.setInput(null);
		}

		fieldTable.refresh();
	}

	public void setInputTagIds(List<String> inputTagIds) {
		this.inputTagIds = inputTagIds;
	}

}
