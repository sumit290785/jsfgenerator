package jsfgenerator.entitymodel.impl;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.entitymodel.AbstractEntityModelBuilder;
import jsfgenerator.entitymodel.forms.EntityListForm;
import jsfgenerator.entitymodel.forms.EntityField;
import jsfgenerator.entitymodel.forms.AbstractEntityForm;
import jsfgenerator.entitymodel.forms.EntityForm;
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
	public void addEntityForm(String viewId, EntityDescription entity, EntityFieldDescription field) {

		AbstractEntityForm form;
		if (field == null) {
			String formName = StringUtils.toDotSeparatedString(viewId, "entity");
			form = new EntityForm(formName, entity.getEntityClassName(), getEntityFields(entity), null);
		} else {
			String formName = StringUtils.toDotSeparatedString(viewId, field.getFieldName());
			form = new EntityForm(formName, entity.getEntityClassName(), getEntityFields(entity), field.getRelationshipToEntity());
		}

		getEntityPageModel(viewId).addForm(form);
	}

	@Override
	public void addEntityListForm(String viewId, EntityDescription domainEntity, EntityFieldDescription listField,
			EntityDescription genericFieldDescription) {

		String simpleListFormName = ClassNameUtils.getSimpleClassName(listField.getFieldName());
		String formName = StringUtils.toDotSeparatedString(viewId, simpleListFormName);
		AbstractEntityForm form = new EntityListForm(formName, domainEntity.getEntityClassName(), genericFieldDescription
				.getEntityClassName(), getEntityFields(genericFieldDescription), listField.getRelationshipToEntity());
		getEntityPageModel(viewId).addForm(form);
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

			if (entityField.getInputTagName() != null && !entityField.getInputTagName().equals("")) {
				EntityField field = new EntityField(entityField.getFieldName(), entityField.getInputTagName());
				fields.add(field);
			}

		}

		return fields;
	}

	@Override
	public void addFieldToList(String viewId, EntityDescription entity, EntityFieldDescription field) {

	}

}
