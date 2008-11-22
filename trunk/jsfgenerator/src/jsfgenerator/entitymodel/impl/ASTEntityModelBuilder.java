package jsfgenerator.entitymodel.impl;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.entitymodel.AbstractEntityModelBuilder;
import jsfgenerator.entitymodel.fields.EntityField;
import jsfgenerator.entitymodel.fields.EntityFieldType;
import jsfgenerator.entitymodel.fields.NumberFieldType;
import jsfgenerator.entitymodel.fields.TextFieldType;
import jsfgenerator.entitymodel.forms.SimpleEntityForm;
import jsfgenerator.entitymodel.pages.EntityPageModel;
import jsfgenerator.entitymodel.pages.AbstractPageModel;
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
	 * @seejsfgenerator.inspector.entitymodel.AbstractEntityModelBuilder#
	 * addComplexEntityFormList(java.lang.Object, java.lang.String)
	 */
	@Override
	public void addComplexEntityFormList(EntityWizardInput entity, String viewId) {
		AbstractPageModel view = getView(viewId);
		
		// TODO
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejsfgenerator.inspector.entitymodel.AbstractEntityModelBuilder#
	 * addSimpleEntityForm(java.lang.Object, java.lang.String)
	 */
	@Override
	public void addSimpleEntityForm(EntityWizardInput entity, String viewId) {
		AbstractPageModel view = getView(viewId);

		if (!(view instanceof EntityPageModel)) {
			throw new IllegalArgumentException("Simple entity form is applicable only for entity page models!");
		}

		EntityPageModel model = (EntityPageModel) view;
		List<EntityField> fields = new ArrayList<EntityField>();
		for (EntityFieldInput input : entity.getFields()) {
			EntityFieldType type = getEntityFieldType(input.getFieldType());
			EntityField field = new EntityField(input.getFieldName(), type);
			fields.add(field);
		}

		SimpleEntityForm form = new SimpleEntityForm(entity.getName(), fields);

		model.addForm(form);
	}
	
	private EntityFieldType getEntityFieldType(Type type) {
		
		if (type.isPrimitiveType()) {
			PrimitiveType primitiveType = (PrimitiveType) type;
			
			if (PrimitiveType.INT == primitiveType.getPrimitiveTypeCode()) {
				return new NumberFieldType(NumberFieldType.INTEGER);
			}
			
			// default for primitive types
			return new TextFieldType(TextFieldType.NONE);
		}
		
		
		if (type.isSimpleType()) {
			SimpleType simpleType = (SimpleType) type;
		}
		
		return new TextFieldType(TextFieldType.MULTILINE);
	}

}
