package jsfgenerator.ui.wizards.editingsupports;

import java.util.ArrayList;

import jsfgenerator.entitymodel.pageelements.EntityRelationship;
import jsfgenerator.generation.common.INameConstants;
import jsfgenerator.ui.model.EntityFieldDescriptionEntityPageWrapper;
import jsfgenerator.ui.wizards.MVCGenerationWizard;
import jsfgenerator.ui.wizards.wizardpages.EntityPageFieldSelectionWizardPage;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

public class InputtagEditingSupport extends EditingSupport {

	private ComboBoxViewerCellEditor editor;

	private EntityPageFieldSelectionWizardPage wizardPage;

	public InputtagEditingSupport(ColumnViewer viewer, EntityPageFieldSelectionWizardPage wizardPage) {
		super(viewer);
		editor = new ComboBoxViewerCellEditor(((TableViewer) viewer).getTable(), SWT.READ_ONLY);
		editor.setContenProvider(new ArrayContentProvider());
		editor.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return (String) element;
			}

		});
		this.wizardPage = wizardPage;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {

		// refresh the drop down
		ArrayList<String> input = new ArrayList<String>();
		input.add(INameConstants.NO_GENERATION_TEXT);

		MVCGenerationWizard wizard = (MVCGenerationWizard) wizardPage.getWizard();

		input.addAll(wizard.getInputTagIds());
		EntityFieldDescriptionEntityPageWrapper entityFieldWrapper = (EntityFieldDescriptionEntityPageWrapper) element;
		if (!EntityRelationship.FIELD.equals(entityFieldWrapper.getEntityFieldDescription().getRelationshipToEntity())
				&& !wizardPage.getSelectedEntityDescription().isEmbedded()) {
			input.add(INameConstants.NON_FIELD_TEXT);
		}
		editor.setInput(input);

		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		EntityFieldDescriptionEntityPageWrapper fieldWrapper = ((EntityFieldDescriptionEntityPageWrapper) element);

		if (fieldWrapper.getEntityDescriptionWrapper() != null) {
			return INameConstants.NON_FIELD_TEXT;
		} else if (fieldWrapper.getEntityFieldDescription().getInputTagName() == null) {
			return INameConstants.NO_GENERATION_TEXT;
		} else {
			return fieldWrapper.getEntityFieldDescription().getInputTagName();
		}
	}

	@Override
	protected void setValue(Object element, Object value) {

		EntityFieldDescriptionEntityPageWrapper entityFieldWrapper = ((EntityFieldDescriptionEntityPageWrapper) element);
		if (value != null && INameConstants.NON_FIELD_TEXT.equals((String) value)
				&& !wizardPage.getSelectedEntityDescription().isEmbedded()) {
			entityFieldWrapper.setExternalForm(entityFieldWrapper.getEntityFieldDescription().getRelationshipToEntity());
			entityFieldWrapper.getEntityFieldDescription().setInputTagName(null);
		} else if (value != null && INameConstants.NO_GENERATION_TEXT.equals((String) value)) {
			entityFieldWrapper.setExternalForm(null);
			entityFieldWrapper.getEntityFieldDescription().setInputTagName(null);
		} else {
			entityFieldWrapper.setExternalForm(null);
			entityFieldWrapper.getEntityFieldDescription().setInputTagName((String) value);
		}

		wizardPage.getMasterPart().refresh();
		getViewer().refresh();
		wizardPage.validate();
	}

}
