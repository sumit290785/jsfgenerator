package jsfgenerator.entitymodel.pageelements;

public class ActionColumnModel extends ColumnModel {

	public static enum ActionType {
		SELECT, DELETE;
	}

	private ActionType type;

	private String idFieldName;

	public ActionColumnModel(String entityClassName, ActionType type, String idFieldName) {
		super(entityClassName, null);
		this.setType(type);
		this.setIdFieldName(idFieldName);
	}

	public void setType(ActionType type) {
		this.type = type;
	}

	public ActionType getType() {
		return type;
	}

	public void setIdFieldName(String idFieldName) {
		this.idFieldName = idFieldName;
	}

	public String getIdFieldName() {
		return idFieldName;
	}

}
