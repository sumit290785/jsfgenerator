package jsfgenerator.generation.utilities;

public class EntityField {
	
	private String name;
	
	private Class<?> type;
	
	public EntityField(String name, Class<?> type) {
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

	public void setType(Class<?> type) {
		this.type = type;
	}

	public Class<?> getType() {
		return type;
	}
	
	

}
