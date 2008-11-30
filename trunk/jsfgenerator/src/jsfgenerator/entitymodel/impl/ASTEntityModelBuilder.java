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
public class ASTEntityModelBuilder extends AbstractEntityModelBuilder<EntityWizardInput> {

	/*
	 * (non-Javadoc)
	 * 
	 * @seejsfgenerator.inspector.entitymodel.AbstractEntityModelBuilder# addComplexEntityFormList(java.lang.Object, java.lang.String)
	 */
	@Override
	public void addComplexEntityFormList(EntityWizardInput entity, String viewId) {
		createEntityForm(entity, viewId, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejsfgenerator.inspector.entitymodel.AbstractEntityModelBuilder# addSimpleEntityForm(java.lang.Object, java.lang.String)
	 */
	@Override
	public void addSimpleEntityForm(EntityWizardInput entity, String viewId) {
		createEntityForm(entity, viewId, true);
	}

	private String getEntityFieldType(Type type) {

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

	private void createEntityForm(EntityWizardInput entity, String viewId, boolean isSimple) {
		AbstractPageModel view = getView(viewId);

		if (!(view instanceof EntityPageModel)) {
			throw new IllegalArgumentException("Simple entity form is applicable only for entity page models!");
		}

		EntityPageModel model = (EntityPageModel) view;
		List<EntityField> fields = new ArrayList<EntityField>();
		for (EntityFieldInput input : entity.getFields()) {
			EntityField field = new EntityField(input.getFieldName(), getEntityFieldType(input.getFieldType()));
			fields.add(field);
		}

		EntityForm form;

		if (isSimple) {
			form = new SimpleEntityForm(entity.getName(), fields);
		} else {
			form = new ComplexEntityFormList(entity.getName(), fields);
		}

		model.addForm(form);
	}

}
