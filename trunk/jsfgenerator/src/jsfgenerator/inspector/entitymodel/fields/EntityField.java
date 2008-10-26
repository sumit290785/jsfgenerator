package jsfgenerator.inspector.entitymodel.fields;

/**
 * Represents a field of an entity in the entity model! It has a name and a type!
 * 
 * @author zoltan verebes
 *
 */
public class EntityField {
	
	private String name;
	
	private EntityFieldType type;
	
	public EntityField(String name, EntityFieldType type) {
		super();
		this.name = name;
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setType(EntityFieldType type) {
		this.type = type;
	}

	public EntityFieldType getType() {
		return type;
	}

}
