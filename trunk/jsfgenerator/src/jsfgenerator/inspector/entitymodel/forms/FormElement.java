package jsfgenerator.inspector.entitymodel.forms;

/**
 * A simple form contains multiple form elements which represents a field of the
 * entity.
 * 
 * @author zoltan verebes
 * 
 */
public class FormElement {

	private String label;

	private String fieldName;
	
	private Class<?> type;

	public FormElement(String label, String fieldName, Class<?> type) {
		this.label = label;
		this.fieldName = fieldName;
		this.type = type;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public Class<?> getType() {
		return type;
	}
}
