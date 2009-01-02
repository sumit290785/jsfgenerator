package jsfgenerator.ui.wizards.editingsupports;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.entitymodel.pageelements.EntityRelationship;
import jsfgenerator.ui.model.AbstractEntityFieldDescriptionWrapper;
import jsfgenerator.ui.model.EntityFieldDescriptionListPageWrapper;
import jsfgenerator.ui.wizards.wizardpages.EntityListPageFieldSelectionWizardPage;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

public class ColumnEditingSupport extends EditingSupport {

	public static final String SHOW = "show column";
	public static final String HIDE = "hide column";

	private ComboBoxViewerCellEditor editor;

	private EntityListPageFieldSelectionWizardPage wizardPage;

	public ColumnEditingSupport(ColumnViewer viewer, EntityListPageFieldSelectionWizardPage wizardPage) {
		super(viewer);
		this.wizardPage = wizardPage;
		editor = new ComboBoxViewerCellEditor(((TableViewer) viewer).getTable(), SWT.READ_ONLY);
		editor.setContenProvider(new ArrayContentProvider());
		editor.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return (String) element;
			}

		});
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		EntityFieldDescriptionListPageWrapper entityFieldWrapper = (EntityFieldDescriptionListPageWrapper) element;
		EntityRelationship rel = entityFieldWrapper.getEntityFieldDescription().getRelationshipToEntity();
		if (EntityRelationship.ONE_TO_MANY.equals(rel) || EntityRelationship.MANY_TO_MANY.equals(rel)) {
			entityFieldWrapper.setExternalForm(rel);
			List<String> fieldNames = new ArrayList<String>();
			for (AbstractEntityFieldDescriptionWrapper fieldWrapper : wizardPage
					.getEntityFieldDescriptionWrappers(entityFieldWrapper.getEntityDescriptionWrapper())) {
				fieldNames.add(fieldWrapper.getEntityFieldDescription().getFieldName());
			}
			editor.setInput(fieldNames);
		} else {
			editor.setInput(new String[] { HIDE, SHOW });
		}

		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		EntityFieldDescriptionListPageWrapper entityFieldWrapper = (EntityFieldDescriptionListPageWrapper) element;

		EntityRelationship rel = entityFieldWrapper.getEntityFieldDescription().getRelationshipToEntity();
		if (entityFieldWrapper.isShown()
				&& (EntityRelationship.ONE_TO_MANY.equals(rel) || EntityRelationship.MANY_TO_MANY.equals(rel))) {
			return entityFieldWrapper.getFieldName();
		} else if (entityFieldWrapper.isShown()) {
			return SHOW;
		} else {
			return HIDE;
		}
	}

	@Override
	protected void setValue(Object element, Object value) {
		EntityFieldDescriptionListPageWrapper entityFieldWrapper = (EntityFieldDescriptionListPageWrapper) element;
		String selectedValue = (String) value;

		EntityRelationship rel = entityFieldWrapper.getEntityFieldDescription().getRelationshipToEntity();
		if (value == null || selectedValue.equals(HIDE)) {
			entityFieldWrapper.setShown(false);
			entityFieldWrapper.setExternalForm(null);
			entityFieldWrapper.setFieldName(null);
		} else if (rel.equals(EntityRelationship.FIELD)) {
			entityFieldWrapper.setShown(true);
			entityFieldWrapper.setExternalForm(null);
			entityFieldWrapper.setFieldName(null);
		} else if (EntityRelationship.ONE_TO_MANY.equals(rel) || EntityRelationship.MANY_TO_MANY.equals(rel)) {
			entityFieldWrapper.setShown(true);
			entityFieldWrapper.setFieldName(selectedValue);
			entityFieldWrapper.setExternalForm(null);
		} else {
			entityFieldWrapper.setShown(true);
			entityFieldWrapper.setExternalForm(entityFieldWrapper.getEntityFieldDescription().getRelationshipToEntity());
			entityFieldWrapper.setFieldName(null);
		}

		wizardPage.getMasterPart().refresh();
		getViewer().refresh();
		wizardPage.validate();
	}

}
