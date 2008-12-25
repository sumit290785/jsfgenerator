package jsfgenerator.entitymodel.forms;

/**
 * Represents a field of an entity in the entity model! It has a name and a
 * type!
 * 
 * @author zoltan verebes
 * 
 */
public class EntityField {

	private String fieldName;

	private String inputTagId;
	
	public EntityField(String name, String inputTagId) {
		this.fieldName = name;
		this.inputTagId = inputTagId;
	}

	/**
	 * name of the field which connects the view and the controller, because it
	 * is used as tag name in the view and also as field name in the particular
	 * controller class
	 * 
	 * @return
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * This id is associated with the id of the input tag in the tag tree
	 * provider. Tag tree provider will look up the specific input tag for the
	 * view by this id
	 * 
	 * @return input tag id
	 */
	public String getInputTagId() {
		return inputTagId;
	}

}
