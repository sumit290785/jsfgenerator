package jsfgenerator.ui.model;

import java.util.List;

import jsfgenerator.entitymodel.forms.EntityRelationship;
import jsfgenerator.generation.common.utilities.ClassNameUtils;

import org.eclipse.jdt.core.dom.TypeDeclaration;

public class EntityFieldDescription {

	private String fieldName;

	private String className;

	private EntityRelationship relationshipToEntity;
	
	private boolean isId;

	private String inputTagId;

	private EntityDescription entityDescription;

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

	public String getInputTagId() {
		return inputTagId;
	}

	public void setInputTagId(String inputTagId) {
		this.inputTagId = inputTagId;
	}

	public void setExternalForm(EntityRelationship relationship) {

		if (relationship == null) {
			this.entityDescription = null;
			return;
		}

		String className;
		if (EntityRelationship.EMBEDDED.equals(relationship) || EntityRelationship.ONE_TO_ONE.equals(relationship)
				|| EntityRelationship.MANY_TO_ONE.equals(relationship)) {
			className = ClassNameUtils.removeGenericParameters(getClassName());
		} else {
			List<String> genericTypeList = ClassNameUtils.getGenericParameterList(getClassName());
			className = ClassNameUtils.removeGenericParameters(genericTypeList.get(0));
		}

		TypeDeclaration typeNode = ProjectResourceProvider.getInstance().findSingleClassTypeDeclaration(className);
		this.entityDescription = new EntityDescription(typeNode);
		this.entityDescription.setEmbedded(true);
	}

	public EntityDescription getEntityDescription() {
		return entityDescription;
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

}
