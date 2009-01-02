package jsfgenerator.ui.wizards.wizardpages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jsfgenerator.generation.common.utilities.NodeNameUtils;
import jsfgenerator.ui.model.AbstractEntityDescriptionWrapper;
import jsfgenerator.ui.model.AbstractEntityFieldDescriptionWrapper;
import jsfgenerator.ui.model.EntityDescription;
import jsfgenerator.ui.wizards.MVCGenerationWizard;

import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
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
public abstract class AbstractFieldSelectionWizardPage extends WizardPage {

	private static final Image IMG_CLASS = JavaPluginImages.get(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CLASS);

	protected class MasterTreeContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {

			if (parentElement instanceof AbstractEntityDescriptionWrapper) {
				AbstractEntityDescriptionWrapper entityDescriptionWrapper = (AbstractEntityDescriptionWrapper) parentElement;
				List<AbstractEntityDescriptionWrapper> embeddedEntityWrappers = new ArrayList<AbstractEntityDescriptionWrapper>();
				for (AbstractEntityFieldDescriptionWrapper entityFieldWrapper : entityDescriptionWrapper.getFieldWrappers()) {
					if (entityFieldWrapper.getEntityDescriptionWrapper() != null) {
						embeddedEntityWrappers.add(entityFieldWrapper.getEntityDescriptionWrapper());
					}
				}

				return embeddedEntityWrappers.toArray();
			}

			return null;
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof AbstractEntityDescriptionWrapper) {
				AbstractEntityDescriptionWrapper entityDescriptionWrapper = (AbstractEntityDescriptionWrapper) element;
				for (AbstractEntityFieldDescriptionWrapper entityFieldWrapper : getEntityFieldDescriptionWrappers(entityDescriptionWrapper)) {
					if (entityFieldWrapper.getEntityDescriptionWrapper() != null) {
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
					return ((AbstractEntityFieldDescriptionWrapper) element).getEntityFieldDescription().getFieldName();
				}

			});
			fieldNameColumn.getColumn().setText("Field");
			fieldNameColumn.getColumn().setWidth(100);

			// type
			final TableViewerColumn typeColumn = new TableViewerColumn(this, SWT.NONE);
			typeColumn.setLabelProvider(new ColumnLabelProvider() {

				public String getText(Object element) {
					return ((AbstractEntityFieldDescriptionWrapper) element).getEntityFieldDescription().getClassName();
				}

			});
			typeColumn.getColumn().setText("Type");
			typeColumn.getColumn().setWidth(200);

			// entity relationship
			final TableViewerColumn entityRelationshipColumn = new TableViewerColumn(this, SWT.NONE);
			entityRelationshipColumn.setLabelProvider(new ColumnLabelProvider() {

				public String getText(Object element) {
					return ((AbstractEntityFieldDescriptionWrapper) element).getEntityFieldDescription()
							.getRelationshipToEntity().getLabel();
				}

			});
			entityRelationshipColumn.getColumn().setText("Entity relationship");
			entityRelationshipColumn.getColumn().setWidth(120);

			// choice
			chocieTagColumn = new TableViewerColumn(this, SWT.NONE);
			chocieTagColumn.setLabelProvider(getChoiceColumnLabelProvider());
			chocieTagColumn.getColumn().setText(getChoiceColumnHeader());
			chocieTagColumn.setEditingSupport(getEditingSupport());
			chocieTagColumn.getColumn().setWidth(200);
		}

	}

	protected TableViewerColumn chocieTagColumn;

	protected TreeViewer masterPart;

	protected Composite detailsPart;

	private FieldTableViewer fieldTable;

	private AbstractEntityDescriptionWrapper selectedEntityDescriptionWrapper;

	public AbstractFieldSelectionWizardPage(String pageName, String title, String description) {
		super(pageName);
		setTitle(title);
		setDescription(description);
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
					selectedEntityDescriptionWrapper.setViewId(null);
					viewFileNameText.setText("");
					controllerClassNameText.setText("");
				} else {
					selectedEntityDescriptionWrapper.setViewId(viewIdText.getText());
					if (NodeNameUtils.isValidViewId(viewIdText.getText())) {
						viewFileNameText.setText(getViewName(viewIdText.getText()));
						controllerClassNameText.setText(getControllerName(viewIdText.getText()));
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
				if (element instanceof AbstractEntityDescriptionWrapper) {
					return ((AbstractEntityDescriptionWrapper) element).getEntityDescription().getEntityClassName();
				}

				return null;
			}

		});

		masterPart.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {

				if (event.getSelection() instanceof StructuredSelection) {
					AbstractEntityDescriptionWrapper selectedEntityDescriptionWrapper = (AbstractEntityDescriptionWrapper) ((StructuredSelection) event
							.getSelection()).getFirstElement();
					refreshFieldTable(selectedEntityDescriptionWrapper);

					if (selectedEntityDescriptionWrapper != null) {
						viewIdComposite.setVisible(!selectedEntityDescriptionWrapper.isEmbedded());
						viewIdText.setText(selectedEntityDescriptionWrapper.getViewId() == null ? ""
								: selectedEntityDescriptionWrapper.getViewId());
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
			List<AbstractEntityDescriptionWrapper> input = getSelectedEntityDescriptionWrappers();
			masterPart.setInput(input);
			refreshFieldTable(null);
			validate();
			if (input != null && input.size() != 0) {
				masterPart.setSelection(new StructuredSelection(input.get(0)));
			}
		} else {
			selectedEntityDescriptionWrapper = null;
		}
	}

	private void refreshFieldTable(AbstractEntityDescriptionWrapper entityDescriptionWrapper) {
		this.selectedEntityDescriptionWrapper = entityDescriptionWrapper;
		if (entityDescriptionWrapper != null) {
			fieldTable.setInput(getAvailableFields(entityDescriptionWrapper));
			detailsPart.setVisible(true);
		} else {
			fieldTable.setInput(null);
			detailsPart.setVisible(false);
		}

		fieldTable.refresh();
	}

	protected List<AbstractEntityFieldDescriptionWrapper> getAvailableFields(
			AbstractEntityDescriptionWrapper entityDescriptionWrapper) {
		List<AbstractEntityFieldDescriptionWrapper> descriptions = new ArrayList<AbstractEntityFieldDescriptionWrapper>();

		for (AbstractEntityFieldDescriptionWrapper fieldWrapper : entityDescriptionWrapper.getFieldWrappers()) {
			if (isAvailable(fieldWrapper)) {
				descriptions.add(fieldWrapper);
			}
		}

		return descriptions;
	}

	public AbstractEntityDescriptionWrapper getSelectedEntityDescription() {
		return selectedEntityDescriptionWrapper;
	}

	public TreeViewer getMasterPart() {
		return masterPart;
	}

	public List<AbstractEntityFieldDescriptionWrapper> getEntityFieldDescriptionWrappers(
			AbstractEntityDescriptionWrapper entityDescriptioWrapper) {
		return entityDescriptioWrapper.getFieldWrappers();
	}

	protected List<AbstractEntityDescriptionWrapper> getSelectedEntityDescriptionWrappers() {
		List<AbstractEntityDescriptionWrapper> entityDescriptionWrappers = new ArrayList<AbstractEntityDescriptionWrapper>();
		for (AbstractEntityDescriptionWrapper entityDescriptionWrapper : getInputList()) {
			if (entityDescriptionWrapper.isPageGenerated()) {
				entityDescriptionWrappers.add(entityDescriptionWrapper);
			}
		}

		return entityDescriptionWrappers;
	}

	public void validate() {
		List<AbstractEntityDescriptionWrapper> entityWrappers = (List<AbstractEntityDescriptionWrapper>) masterPart.getInput();
		/*
		 * validate for view id
		 */
		for (AbstractEntityDescriptionWrapper entityWrapper : entityWrappers) {
			if (entityWrapper.getViewId() == null || !NodeNameUtils.isValidViewId(entityWrapper.getViewId())) {
				setErrorMessage("Incorrect view id! Entity: " + entityWrapper.getEntityDescription().getEntityClassName());
				setPageComplete(false);
				return;
			}
		}

		String message = ((MVCGenerationWizard) getWizard()).validateViewId();
		if (message != null) {
			setErrorMessage(message);
			setPageComplete(false);
			return;
		}

		setErrorMessage(null);
		setPageComplete(true);
	}

	protected abstract EditingSupport getEditingSupport();

	protected abstract String getChoiceColumnHeader();

	protected abstract CellLabelProvider getChoiceColumnLabelProvider();

	protected abstract boolean isAvailable(AbstractEntityFieldDescriptionWrapper fieldWrapper);

	protected abstract List<? extends AbstractEntityDescriptionWrapper> getInputList();

	protected abstract String getViewName(String text);

	protected abstract String getControllerName(String text);

}
