package jsfgenerator.entitymodel.forms;

import java.util.List;

/**
 * A simple entity form describes the simple and embedded fields of an entity and supports commands for editing, saving, cancel editing of
 * the entity.
 * 
 * @author zoltan verebes
 * 
 * @param <T>
 */
public class SimpleEntityForm extends EntityForm {

	private String entityClassName;

	public SimpleEntityForm(String entityName, String entityClassName, List<EntityField> fields, EntityRelationship relationship) {
		super(entityName, fields, relationship);
		this.entityClassName = entityClassName;
	}

	public String getEntityClassName() {
		return entityClassName;
	}

}
