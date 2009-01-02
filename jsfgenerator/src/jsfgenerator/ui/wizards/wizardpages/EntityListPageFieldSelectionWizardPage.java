package jsfgenerator.ui.wizards.wizardpages;

import java.util.List;

import jsfgenerator.entitymodel.pageelements.EntityRelationship;
import jsfgenerator.generation.common.utilities.NodeNameUtils;
import jsfgenerator.ui.model.AbstractEntityDescriptionWrapper;
import jsfgenerator.ui.model.AbstractEntityFieldDescriptionWrapper;
import jsfgenerator.ui.model.EntityDescriptionListPageWrapper;
import jsfgenerator.ui.model.EntityFieldDescriptionListPageWrapper;
import jsfgenerator.ui.wizards.MVCGenerationWizard;
import jsfgenerator.ui.wizards.editingsupports.ColumnEditingSupport;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;

public class EntityListPageFieldSelectionWizardPage extends AbstractFieldSelectionWizardPage {

	public EntityListPageFieldSelectionWizardPage() {
		super("EntityListPageFieldSelectionWizardPage", "Entity field selection",
				"Select the fields for columns of the entity list pages");
	}

	@Override
	protected List<AbstractEntityFieldDescriptionWrapper> getAvailableFields(
			AbstractEntityDescriptionWrapper entityDescriptionWrapper) {
		return entityDescriptionWrapper.getFieldWrappers();
	}

	@Override
	protected String getChoiceColumnHeader() {
		return "show / hide";
	}

	@Override
	protected CellLabelProvider getChoiceColumnLabelProvider() {
		return new ColumnLabelProvider() {

			public String getText(Object element) {
				EntityFieldDescriptionListPageWrapper fieldWrapper = (EntityFieldDescriptionListPageWrapper) element;
				EntityRelationship rel = fieldWrapper.getEntityFieldDescription().getRelationshipToEntity();
				if (fieldWrapper.isShown() && !rel.equals(EntityRelationship.ONE_TO_MANY)
						&& !rel.equals(EntityRelationship.MANY_TO_MANY)) {
					return ColumnEditingSupport.SHOW;
				} else if (fieldWrapper.isShown()
						&& (rel.equals(EntityRelationship.ONE_TO_MANY) || rel.equals(EntityRelationship.MANY_TO_MANY))) {
					return fieldWrapper.getFieldName();
				} else {
					return ColumnEditingSupport.HIDE;
				}
			}
		};
	}

	@Override
	protected EditingSupport getEditingSupport() {
		return new ColumnEditingSupport(chocieTagColumn.getViewer(), this);
	}

	@Override
	protected boolean isAvailable(AbstractEntityFieldDescriptionWrapper fieldWrapper) {
		return true;
	}

	@Override
	protected List<EntityDescriptionListPageWrapper> getInputList() {
		return ((MVCGenerationWizard) getWizard()).getEntityDescriptionListPageWrappers();
	}

	@Override
	protected String getControllerName(String text) {
		return NodeNameUtils.getListPageClassFileNameByUniqueName(text);
	}

	@Override
	protected String getViewName(String text) {
		return NodeNameUtils.getListPageViewNameByUniqueName(text);
	}
}
