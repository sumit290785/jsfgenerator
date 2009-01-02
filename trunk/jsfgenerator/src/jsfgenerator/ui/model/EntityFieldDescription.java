package jsfgenerator.ui.model;

import jsfgenerator.entitymodel.pageelements.EntityRelationship;

public class EntityFieldDescription {

	private String fieldName;

	private String className;

	private EntityRelationship relationshipToEntity;

	private boolean isId;
	
	private String inputTagName;
	
	public EntityFieldDescription(String fieldName, String className, EntityRelationship relationshipToEntity, boolean isId) {
		this.fieldName = fieldName;
		this.className = className;
		this.relationshipToEntity = relationshipToEntity;
		this.isId = isId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getClassName() {
		return className;
	}

	public void setRelationshipToEntity(EntityRelationship relationshipToEntity) {
		this.relationshipToEntity = relationshipToEntity;
	}

	public EntityRelationship getRelationshipToEntity() {
		return relationshipToEntity;
	}

	public boolean isId() {
		return isId;
	}

	public void setInputTagName(String inputTagName) {
		this.inputTagName = inputTagName;
	}

	public String getInputTagName() {
		return inputTagName;
	}

}
