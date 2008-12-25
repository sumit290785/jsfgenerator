package jsfgenerator.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jsfgenerator.entitymodel.forms.EntityRelationship;
import jsfgenerator.generation.common.utilities.NodeNameUtils;
import jsfgenerator.ui.model.EntityDescription;
import jsfgenerator.ui.model.EntityFieldDescription;

import org.eclipse.jdt.internal.ui.JavaPluginImages;
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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings("restriction")
public class EntityClassFieldSelectionWizardPage extends WizardPage {

	private static final String NON_FIELD_TEXT = "Generate external form";
	private static final String NO_GENERATION_TEXT = "Do not generate";

	private static final Image IMG_CLASS = JavaPluginImages.get(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CLASS);

	protected class InputTagEditingSupport extends EditingSupport {

		private ComboBoxViewerCellEditor editor;

		public InputTagEditingSupport(ColumnViewer viewer) {
			super(viewer);
			editor = new ComboBoxViewerCellEditor(((TableViewer) viewer).getTable(), SWT.READ_ONLY);
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
			input.add(NO_GENERATION_TEXT);
			input.addAll(inputTagIds);
			EntityFieldDescription entityField = (EntityFieldDescription) element;
			if (!EntityRelationship.FIELD.equals(entityField.getRelationshipToEntity())
					&& !selectedEntityDescription.isEmbedded()) {
				input.add(NON_FIELD_TEXT);
			}
			editor.setInput(input);

			return editor;
		}

		@Override
		protected Object getValue(Object element) {
			EntityFieldDescription entityField = ((EntityFieldDescription) element);

			if (entityField.getEntityDescription() != null) {
				return NON_FIELD_TEXT;
			} else if (entityField.getInputTagId() == null) {
				return NO_GENERATION_TEXT;
			} else {
				return entityField.getInputTagId();
			}
		}

		@Override
		protected void setValue(Object element, Object value) {

			EntityFieldDescription entityField = ((EntityFieldDescription) element);
			if (value != null && NON_FIELD_TEXT.equals((String) value) && !selectedEntityDescription.isEmbedded()) {
				entityField.setExternalForm(entityField.getRelationshipToEntity());
				entityField.setInputTagId(null);
			} else if (value != null && NO_GENERATION_TEXT.equals((String) value)) {
				entityField.setExternalForm(null);
				entityField.setInputTagId(null);
			} else {
				entityField.setExternalForm(null);
				entityField.setInputTagId((String) value);
			}

			masterPart.refresh();
			getViewer().refresh();
			validate();
		}
	}

	protected class MasterTreeContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {

			if (parentElement instanceof EntityDescription) {
				EntityDescription entityDescription = (EntityDescription) parentElement;
				List<EntityDescription> embeddedEntityDescription = new ArrayList<EntityDescription>();
				for (EntityFieldDescription entityField : entityDescription.getEntityFieldDescriptions()) {
					if (entityField.getEntityDescription() != null) {
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
					if (entityField.getEntityDescription() != null) {
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
			final TableViewerColumn fieldNameColumn = new TableViewerColumn(this, SWT.NONE);
			fieldNameColumn.setLabelProvider(new ColumnLabelProvider() {

				public String getText(Object element) {
					return ((EntityFieldDescription) element).getFieldName();
				}

			});
			fieldNameColumn.getColumn().setText("Field");
			fieldNameColumn.getColumn().setWidth(100);

			// type
			final TableViewerColumn typeColumn = new TableViewerColumn(this, SWT.NONE);
			typeColumn.setLabelProvider(new ColumnLabelProvider() {

				public String getText(Object element) {
					return ((EntityFieldDescription) element).getClassName();
				}

			});
			typeColumn.getColumn().setText("Type");
			typeColumn.getColumn().setWidth(200);

			// entity relationship
			final TableViewerColumn entityRelationshipColumn = new TableViewerColumn(this, SWT.NONE);
			entityRelationshipColumn.setLabelProvider(new ColumnLabelProvider() {

				public String getText(Object element) {
					return ((EntityFieldDescription) element).getRelationshipToEntity().getLabel();
				}

			});
			entityRelationshipColumn.getColumn().setText("Entity relationship");
			entityRelationshipColumn.getColumn().setWidth(120);

			// inputtag id
			final TableViewerColumn inputTagColumn = new TableViewerColumn(this, SWT.NONE);
			inputTagColumn.setLabelProvider(new ColumnLabelProvider() {

				public String getText(Object element) {
					EntityFieldDescription entityField = (EntityFieldDescription) element;
					if (entityField.getEntityDescription() != null) {
						return NON_FIELD_TEXT;
					} else if (entityField.getInputTagId() == null) {
						return NO_GENERATION_TEXT;
					} else {
						return ((EntityFieldDescription) element).getInputTagId();
					}
				}

			});
			inputTagColumn.getColumn().setText("Input tag id");
			inputTagColumn.getColumn().setWidth(200);
			inputTagColumn.setEditingSupport(new InputTagEditingSupport(this));
		}

	}

	protected TreeViewer masterPart;

	protected Composite detailsPart;

	private FieldTableViewer fieldTable;

	private List<String> inputTagIds;

	private EntityDescription selectedEntityDescription;

	protected EntityClassFieldSelectionWizardPage() {
		super("EntityClassFieldSelectionWizardPage");
		setTitle("Entity field selection");
		setDescription("Please, select the fields of the entities and their views");
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);
		setControl(composite);

		composite.setLayout(new GridLayout(2, false));

		masterPart = new TreeViewer(composite, SWT.BORDER);
		masterPart.setContentProvider(new MasterTreeContentProvider());
		final GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.FILL;
		gridData.widthHint = 250;
		masterPart.getControl().setLayoutData(gridData);

		detailsPart = new Composite(composite, SWT.NONE);
		detailsPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		detailsPart.setLayout(new GridLayout(1, false));

		fieldTable = new FieldTableViewer(detailsPart, SWT.BORDER);
		fieldTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fieldTable.getTable().setLinesVisible(true);
		fieldTable.getTable().setHeaderVisible(true);

		final Composite viewIdComposite = new Composite(detailsPart, SWT.BORDER);
		viewIdComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM | SWT.FILL, false, false));
		viewIdComposite.setLayout(new GridLayout(2, false));

		final Label viewIdLabel = new Label(viewIdComposite, SWT.NONE);
		viewIdLabel.setText("View id: ");
		viewIdLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING | SWT.CENTER, false, false));
		final Text viewIdText = new Text(viewIdComposite, SWT.BORDER);
		viewIdText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Label viewFileNameLabel = new Label(viewIdComposite, SWT.NONE);
		viewFileNameLabel.setText("View file name: ");
		viewFileNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING | SWT.CENTER, false, false));
		final Text viewFileNameText = new Text(viewIdComposite, SWT.BORDER | SWT.READ_ONLY);
		viewFileNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Label controllerClassNameLabel = new Label(viewIdComposite, SWT.NONE);
		controllerClassNameLabel.setText("Controller class name: ");
		controllerClassNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING | SWT.CENTER, false, false));
		final Text controllerClassNameText = new Text(viewIdComposite, SWT.BORDER | SWT.READ_ONLY);
		controllerClassNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		viewIdText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				if (viewIdText.getText() == null || viewIdText.getText().equals("")) {
					selectedEntityDescription.setViewId(null);
					viewFileNameText.setText("");
					controllerClassNameText.setText("");
				} else {
					selectedEntityDescription.setViewId(viewIdText.getText());
					if (NodeNameUtils.isValidViewId(viewIdText.getText())) {
						viewFileNameText.setText(NodeNameUtils.getEntityPageViewNameByUniqueName(viewIdText.getText()));
						controllerClassNameText.setText(NodeNameUtils
								.getEntityPageClassFileNameByUniqueName(viewIdText.getText()));
					}
				}

				validate();
			}
		});

		masterPart.setLabelProvider(new LabelProvider() {

			@Override
			public Image getImage(Object element) {
				if (element instanceof EntityDescription) {
					return IMG_CLASS;
				}

				return super.getImage(element);
			}

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

					if (selectedEntityDescription != null) {
						viewIdComposite.setVisible(!selectedEntityDescription.isEmbedded());
						viewIdText.setText(selectedEntityDescription.getViewId() == null ? "" : selectedEntityDescription
								.getViewId());
					}
				}
			}
		});

		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			List<EntityDescription> input = getSelectedEntityPageEntityDescriptions();
			masterPart.setInput(input);
			refreshFieldTable(null);

			List<String> inputTagIds = ((MVCGenerationWizard) getWizard()).getInputTagIds();
			this.inputTagIds = inputTagIds;
			validate();
		} else {
			selectedEntityDescription = null;
		}
	}

	@SuppressWarnings("unchecked")
	private void validate() {
		List<EntityDescription> entityDescirptions = (List<EntityDescription>) masterPart.getInput();
		/*
		 * validate for view id
		 */
		for (EntityDescription entityDescription : entityDescirptions) {
			if (entityDescription.getViewId() == null || !NodeNameUtils.isValidViewId(entityDescription.getViewId())) {
				setErrorMessage("Incorrect view id! Entity: " + entityDescription.getEntityClassName());
				setPageComplete(false);
				return;
			}
		}

		setErrorMessage(null);
		setPageComplete(true);
	}

	private List<EntityDescription> getSelectedEntityPageEntityDescriptions() {
		List<EntityDescription> entityDescriptions = new ArrayList<EntityDescription>();
		for (EntityDescription entityDescription : ((MVCGenerationWizard) getWizard()).getEntityDescriptions()) {
			if (entityDescription.isEntityPage()) {
				entityDescriptions.add(entityDescription);
			}
		}

		return entityDescriptions;
	}

	private void refreshFieldTable(EntityDescription entityDescription) {
		this.selectedEntityDescription = entityDescription;
		if (entityDescription != null) {
			fieldTable.setInput(getAvailableFields(entityDescription));
			detailsPart.setVisible(true);
		} else {
			fieldTable.setInput(null);
			detailsPart.setVisible(false);
		}

		fieldTable.refresh();
	}

	private List<EntityFieldDescription> getAvailableFields(EntityDescription entityDescription) {
		List<EntityFieldDescription> descriptions = new ArrayList<EntityFieldDescription>();

		for (EntityFieldDescription field : entityDescription.getEntityFieldDescriptions()) {
			if (!field.isId()) {
				descriptions.add(field);
			}
		}

		return descriptions;
	}

}
