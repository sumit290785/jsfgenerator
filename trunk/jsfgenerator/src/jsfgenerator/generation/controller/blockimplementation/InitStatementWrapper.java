package jsfgenerator.generation.controller.blockimplementation;

public class InitStatementWrapper {

	public static enum EditorType {
		EDIT_HELPER, LIST_EDIT_HELPER
	}

	private EditorType editorType;

	private String fieldName;

	private String entityClass;
	
	private String entityFieldName;

	public InitStatementWrapper(EditorType editorType, String fieldName, String entityClass, String entityFieldName) {
		this.editorType = editorType;
		this.fieldName = fieldName;
		this.entityClass = entityClass;
		this.entityFieldName = entityFieldName;
	}

	public EditorType getEditorType() {
		return editorType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getEntityClass() {
		return entityClass;
	}

	public String getEntityFieldName() {
		return entityFieldName;
	}

}
