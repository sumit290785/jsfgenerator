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
import jsfgenerator.generation.common.utilities.NodeNameUtils;
import jsfgenerator.ui.wizards.EntityWizardInput;
import jsfgenerator.ui.wizards.EntityWizardInput.EntityFieldInput;

import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;

/**
 * Entity model builder class for abstract syntax trees, provided by eclipse!
 * 
 * @author zoltan verebes
 * 
 */
public class ASTEntityModelBuilder extends AbstractEntityModelBuilder<EntityWizardInput, EntityFieldInput> {

	/*
	 * (non-Javadoc)
	 * 
	 * @seejsfgenerator.inspector.entitymodel.AbstractEntityModelBuilder# addSimpleEntityForm(java.lang.String, java.lang.Object)
	 */
	@Override
	public void addSimpleEntityForm(String viewId, EntityWizardInput entity) {
		String formName = NodeNameUtils.getCanonicalName(viewId, entity.getName());
		String className = entity.getName();

		EntityForm form = new SimpleEntityForm(formName, className, getEntityFields(entity));
		getEntityPageModel(viewId).addForm(form);
	}

	@Override
	public void addComplexEntityFormList(String viewId, EntityWizardInput domainEntity, EntityFieldInput listField, EntityWizardInput genericEntity) {
		String formName = NodeNameUtils.getCanonicalName(viewId, domainEntity.getName(), listField.getFieldName());
		String className = genericEntity.getName();

		EntityForm form = new ComplexEntityFormList(formName, genericEntity.getName(), className, getEntityFields(genericEntity));
		getEntityPageModel(viewId).addForm(form);
	}

	protected EntityPageModel getEntityPageModel(String viewId) {
		AbstractPageModel view = getView(viewId);

		if (!(view instanceof EntityPageModel)) {
			throw new IllegalArgumentException("Simple entity form is applicable only for entity page models!");
		}

		return (EntityPageModel) view;
	}

	protected List<EntityField> getEntityFields(EntityWizardInput entity) {
		List<EntityField> fields = new ArrayList<EntityField>();
		for (EntityFieldInput input : entity.getFields()) {
			EntityField field = new EntityField(input.getFieldName(), getEntityFieldType(input.getFieldType()));
			fields.add(field);
		}

		return fields;
	}

	protected String getEntityFieldType(Type type) {

		// TODO: implement style if i have time to do

		if (type.isPrimitiveType()) {
			PrimitiveType primitiveType = (PrimitiveType) type;

			if (PrimitiveType.INT == primitiveType.getPrimitiveTypeCode()) {
				return "FREE_TEXT_INPUT";
			}

			// default for primitive types
			return "FREE_TEXT_INPUT";
		}

		// TODO:
		if (type.isSimpleType()) {
			SimpleType simpleType = (SimpleType) type;
		}

		return "FREE_TEXT_INPUT_MULTILINE";
	}

}
