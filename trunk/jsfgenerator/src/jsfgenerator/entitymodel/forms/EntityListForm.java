package jsfgenerator.entitymodel.forms;

import java.util.List;

/**
 * Maintain a list of entity forms and supports commands for adding, removing actions!
 * 
 * @author zoltan verebes
 */
public class EntityListForm extends AbstractEntityForm {

	private EntityForm entityForm;

	public EntityListForm(String entityName, String genericEntityName, String genericEntityClassName,
			List<EntityField> fields, EntityRelationship relationship) {
		super(entityName, fields, relationship);
		entityForm = new EntityForm(entityName, genericEntityClassName, fields, relationship);
	}

	public EntityForm getEntityForm() {
		return entityForm;
	}

}
