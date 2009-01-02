package jsfgenerator.entitymodel.pageelements;

/**
 * it represents a column of an entity list page. Column definition is nothing, but a field of an entity.
 * 
 * @author zoltan verebes
 */
public class ColumnModel {

	private String entityClassName;

	private String fieldName;

	public ColumnModel(String entityClassName, String fieldName) {
		this.entityClassName = entityClassName;
		this.fieldName = fieldName;
	}

	public String getEntityClassName() {
		return entityClassName;
	}

	public String getFieldName() {
		return fieldName;
	}

}
