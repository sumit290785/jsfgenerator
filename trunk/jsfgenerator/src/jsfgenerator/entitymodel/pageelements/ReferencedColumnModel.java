package jsfgenerator.entitymodel.pageelements;

/**
 * A field is referenced when the entity is not the domain entity and its relationship to the domain entity is one to many or many to many.
 * 
 * @author zoltan verebes
 * 
 */
public class ReferencedColumnModel extends ColumnModel {

	private String referencedEntityClassName;

	private String referencedFieldName;

	private EntityRelationship relationshipToDomainEntity;

	public ReferencedColumnModel(String entityClassName, String fieldName, String referencedEntityClassName,
			String referencedFieldName, EntityRelationship relationshipToDomainEntity) {
		super(entityClassName, fieldName);
		this.referencedEntityClassName = referencedEntityClassName;
		this.referencedFieldName = referencedFieldName;
		this.relationshipToDomainEntity = relationshipToDomainEntity;
	}

	public String getReferencedEntityClassName() {
		return referencedEntityClassName;
	}

	public String getReferencedFieldName() {
		return referencedFieldName;
	}

	public EntityRelationship getRelationshipToDomainEntity() {
		return relationshipToDomainEntity;
	}

}
