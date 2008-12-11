package jsfgenerator.ui.model;

import org.eclipse.jdt.core.dom.TypeDeclaration;


public class EntityFieldDescription {

	private String fieldName;

	private String className;

	private boolean isCollectionOfEntity;

	private String inputTagId;

	private EntityDescription entityDescription;

	private boolean isCollectionInComplexForm;

	public EntityFieldDescription(String fieldName, String className, boolean isCollectionOfEntity) {
		this.fieldName = fieldName;
		this.className = className;
		this.isCollectionOfEntity = isCollectionOfEntity;
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

	public void setCollectionOfEntity(boolean isCollectionOfEntity) {
		this.isCollectionOfEntity = isCollectionOfEntity;
	}

	public boolean isCollectionOfEntity() {
		return isCollectionOfEntity;
	}

	public void setCollectionInComplexForm(String genericClassName) {
		if (genericClassName == null || genericClassName.equals("")) {
			this.isCollectionInComplexForm = false;
			this.entityDescription = null;
		} else {
			TypeDeclaration typeNode = ProjectResourceProvider.getInstance().findSingleClassTypeDeclaration(genericClassName);
			this.isCollectionInComplexForm = true;
			this.entityDescription = new EntityDescription(typeNode);
			this.entityDescription.setEmbedded(true);
		}

	}

	public boolean isCollectionInComplexForm() {
		return isCollectionInComplexForm;
	}

	public EntityDescription getEntityDescription() {
		return entityDescription;
	}

}
