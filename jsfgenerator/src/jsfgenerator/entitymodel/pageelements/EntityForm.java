package jsfgenerator.entitymodel.pageelements;

import java.util.List;

/**
 * A simple entity form describes the simple and embedded fields of an entity and supports commands for editing, saving, cancel editing of
 * the entity.
 * 
 * @author zoltan verebes
 */
public class EntityForm extends AbstractEntityForm {

	private String entityClassName;

	public EntityForm(String entityName, String entityClassName, List<EntityField> fields, EntityRelationship relationship) {
		super(entityName, fields, relationship);
		this.entityClassName = entityClassName;
	}

	public String getEntityClassName() {
		return entityClassName;
	}

}
