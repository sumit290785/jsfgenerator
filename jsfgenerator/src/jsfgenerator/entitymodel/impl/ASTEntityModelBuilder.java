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
import jsfgenerator.generation.common.utilities.NodeNameUtils;
import jsfgenerator.ui.model.EntityDescription;
import jsfgenerator.ui.model.EntityFieldDescription;

/**
 * Entity model builder class for abstract syntax trees, provided by eclipse!
 * 
 * @author zoltan verebes
 * 
 */
public class ASTEntityModelBuilder extends AbstractEntityModelBuilder<EntityDescription, EntityFieldDescription> {

	/*
	 * (non-Javadoc)
	 * 
	 * @seejsfgenerator.inspector.entitymodel.AbstractEntityModelBuilder# addSimpleEntityForm(java.lang.String, java.lang.Object)
	 */
	@Override
	public void addSimpleEntityForm(String viewId, EntityDescription entity) {
		String entityClassSimpleName = ClassNameUtils.getSimpleClassName(entity.getEntityClassName());
		String formName = NodeNameUtils.getCanonicalName(viewId, entityClassSimpleName);

		EntityForm form = new SimpleEntityForm(formName, entity.getEntityClassName(), getEntityFields(entity));
		getEntityPageModel(viewId).addForm(form);
	}

	@Override
	public void addComplexEntityFormList(EntityDescription domainEntity, EntityFieldDescription listField) {
		EntityDescription genericFieldDescription = listField.getEntityDescription();

		String simpleFormName = ClassNameUtils.getSimpleClassName(domainEntity.getEntityClassName());
		String simpleListFormName = ClassNameUtils.getSimpleClassName(listField.getFieldName());
		String formName = NodeNameUtils.getCanonicalName(domainEntity.getViewId(), simpleFormName, simpleListFormName);

		EntityForm form = new ComplexEntityFormList(formName, simpleFormName, genericFieldDescription.getEntityClassName(),
				getEntityFields(genericFieldDescription));
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

			if (!entityField.isCollectionInComplexForm()) {
				EntityField field = new EntityField(entityField.getFieldName(), entityField.getInputTagId());
				fields.add(field);
			}

		}

		return fields;
	}

}
