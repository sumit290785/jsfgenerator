package jsfgenerator.entitymodel.forms;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract super class of a simple entity form or an entity list which contains multiple entity forms!
 * 
 * A form has three tasks! A simple form is a container of an entity's fields, and handles basic CRUD commands! This class supports Command
 * design pattern for handling commands on the entity!
 * 
 * The third task is to maintain other forms which are also EntityForms.
 * 
 * @author zoltan verebes
 * 
 */
public abstract class EntityForm {

	private String entityName;

	private List<EntityField> fields = new ArrayList<EntityField>();

	private EntityRelationship relationshipToEntity;

	public EntityForm(String entityName, List<EntityField> fields, EntityRelationship relationshipToEntity) {
		this.entityName = entityName;
		this.fields = fields;
		this.relationshipToEntity = relationshipToEntity;
	}

	public String getEntityName() {
		return entityName;
	}

	public List<EntityField> getFields() {
		return fields;
	}

	public EntityRelationship getRelationshipToEntity() {
		return relationshipToEntity;
	}

}
