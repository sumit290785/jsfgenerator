package jsfgenerator.entitymodel.forms;

public enum EntityRelationship {
	
	FIELD("Field"),
	
	EMBEDDED("Embedded"),
	
	ONE_TO_ONE("One to one"),
	
	ONE_TO_MANY("One to many"),
	
	MANY_TO_ONE("Many to one"),
	
	MANY_TO_MANY("Many to many");
	
	String label;
	
	EntityRelationship(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
	public boolean increaseHierarchy() {
		return !this.equals(FIELD) && !equals(EMBEDDED);
	}

}
