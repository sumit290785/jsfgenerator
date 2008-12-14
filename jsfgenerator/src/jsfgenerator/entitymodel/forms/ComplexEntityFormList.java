package jsfgenerator.entitymodel.forms;

import java.util.List;

/**
 * Maintain a list of entity forms and supports commands for adding, removing actions!
 * 
 * @author zoltan verebes
 */
public class ComplexEntityFormList extends EntityForm {

	private SimpleEntityForm simpleForm;

	public ComplexEntityFormList(String entityName, String genericEntityName, String genericEntityClassName, List<EntityField> fields, EntityRelationship relationship) {
		super(entityName, fields, relationship);
		simpleForm = new SimpleEntityForm(entityName + "." + genericEntityName, genericEntityClassName, fields, relationship);
	}

	public SimpleEntityForm getSimpleForm() {
		return simpleForm;
	}

}
