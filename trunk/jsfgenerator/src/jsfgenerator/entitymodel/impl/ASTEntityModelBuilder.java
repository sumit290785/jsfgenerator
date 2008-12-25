package jsfgenerator.entitymodel.impl;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.entitymodel.AbstractEntityModelBuilder;
import jsfgenerator.entitymodel.forms.ComplexEntityFormList;
import jsfgenerator.entitymodel.forms.EntityField;
import jsfgenerator.entitymodel.forms.EntityForm;
import jsfgenerator.entitymodel.forms.SimpleEntityForm;
import jsfgenerator.entitymodel.pages.AbstractPageModel;
import jsfgenerator.entitymodel.pages.EntityPageModel;
import jsfgenerator.generation.common.utilities.ClassNameUtils;
import jsfgenerator.generation.common.utilities.StringUtils;
import jsfgenerator.ui.model.EntityDescription;
import jsfgenerator.ui.model.EntityFieldDescription;

/**
 * Entity model builder class for abstract syntax trees, provided by eclipse!
 * 
 * @author zoltan verebes
 * 
 */
public class ASTEntityModelBuilder extends AbstractEntityModelBuilder<EntityDescription, EntityFieldDescription> {

	@Override
	public void addSimpleEntityForm(String viewId, EntityDescription entity, EntityFieldDescription field) {

		EntityForm form;
		if (field == null) {
			String formName = StringUtils.toDotSeparatedString(viewId, "entity");
			form = new SimpleEntityForm(formName, entity.getEntityClassName(), getEntityFields(entity), null);
		} else {
			String formName = StringUtils.toDotSeparatedString(viewId, field.getFieldName());
			form = new SimpleEntityForm(formName, entity.getEntityClassName(), getEntityFields(entity), field
					.getRelationshipToEntity());
		}

		getEntityPageModel(viewId).addForm(form);
	}

	@Override
	public void addComplexEntityFormList(EntityDescription domainEntity, EntityFieldDescription listField) {
		EntityDescription genericFieldDescription = listField.getEntityDescription();

		String simpleListFormName = ClassNameUtils.getSimpleClassName(listField.getFieldName());
		String formName = StringUtils.toDotSeparatedString(domainEntity.getViewId(), simpleListFormName);
		EntityForm form = new ComplexEntityFormList(formName, domainEntity.getEntityClassName(), genericFieldDescription
				.getEntityClassName(), getEntityFields(genericFieldDescription), listField.getRelationshipToEntity());
		getEntityPageModel(domainEntity.getViewId()).addForm(form);
	}

	protected EntityPageModel getEntityPageModel(String viewId) {
		AbstractPageModel view = getView(viewId);

		if (!(view instanceof EntityPageModel)) {
			throw new IllegalArgumentException("Simple entity form is applicable only for entity page models!");
		}

		return (EntityPageModel) view;
	}

	protected List<EntityField> getEntityFields(EntityDescription entity) {
		List<EntityField> fields = new ArrayList<EntityField>();
		for (EntityFieldDescription entityField : entity.getEntityFieldDescriptions()) {

			if (entityField.getEntityDescription() == null && entityField.getInputTagId() != null
					&& !entityField.getInputTagId().equals("")) {
				EntityField field = new EntityField(entityField.getFieldName(), entityField.getInputTagId());
				fields.add(field);
			}

		}

		return fields;
	}

}
