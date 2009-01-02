package jsfgenerator.ui.wizards.wizardpages;

import java.util.List;

import jsfgenerator.generation.common.INameConstants;
import jsfgenerator.generation.common.utilities.NodeNameUtils;
import jsfgenerator.ui.model.AbstractEntityFieldDescriptionWrapper;
import jsfgenerator.ui.model.EntityDescriptionEntityPageWrapper;
import jsfgenerator.ui.model.EntityFieldDescriptionEntityPageWrapper;
import jsfgenerator.ui.wizards.MVCGenerationWizard;
import jsfgenerator.ui.wizards.editingsupports.InputtagEditingSupport;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;

public class EntityPageFieldSelectionWizardPage extends AbstractFieldSelectionWizardPage {

	public EntityPageFieldSelectionWizardPage() {
		super("EntityPageFieldSelectionWizardPage", "Entity field selection", "Select the fields of the entity pages");
	}

	@Override
	protected boolean isAvailable(AbstractEntityFieldDescriptionWrapper fieldWrapper) {
		return !fieldWrapper.getEntityFieldDescription().isId();
	}

	@Override
	protected String getChoiceColumnHeader() {
		return "Input tag";
	}

	@Override
	protected CellLabelProvider getChoiceColumnLabelProvider() {
		return new ColumnLabelProvider() {

			public String getText(Object element) {
				EntityFieldDescriptionEntityPageWrapper entityFieldWrapper = (EntityFieldDescriptionEntityPageWrapper) element;
				if (entityFieldWrapper.getEntityDescriptionWrapper() != null) {
					return INameConstants.NON_FIELD_TEXT;
				} else if (entityFieldWrapper.getEntityFieldDescription().getInputTagName() == null) {
					return INameConstants.NO_GENERATION_TEXT;
				} else {
					return entityFieldWrapper.getEntityFieldDescription().getInputTagName();
				}
			}
		};
	}

	@Override
	protected EditingSupport getEditingSupport() {
		return new InputtagEditingSupport(chocieTagColumn.getViewer(), this);
	}

	@Override
	protected List<EntityDescriptionEntityPageWrapper> getInputList() {
		return ((MVCGenerationWizard) getWizard()).getEntityDescriptionEntityPageWrappers();
	}
	
	@Override
	protected String getControllerName(String text) {
		return NodeNameUtils.getEntityPageClassFileNameByUniqueName(text);
	}

	@Override
	protected String getViewName(String text) {
		return NodeNameUtils.getEntityPageViewNameByUniqueName(text);
	}
}
