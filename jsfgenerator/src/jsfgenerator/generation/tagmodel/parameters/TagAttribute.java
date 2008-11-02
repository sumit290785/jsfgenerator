package jsfgenerator.generation.tagmodel.parameters;

/**
 * A tag parameter is a key-value pair! A parameter has either a static value or
 * an expression which is changed to an EL value at view generation time
 * 
 * @author zoltan verebes
 * 
 */
public class TagAttribute {

	/**
	 * A tag parameter either a static parameter or an expression! Expressions
	 * are changed to expression language (EL) value at view generation time
	 */
	public enum TagParameterType {
		STATIC, EXPRESSION
	}
	
	public TagAttribute(String name, String value) {
		this.name = name;
		this.value = value;
	}

	// left value of the parameter
	protected String name;

	// right value of the parameter
	protected String value;

	// type of the parameter which is either a static value or an EL value
	protected TagParameterType type;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setType(TagParameterType type) {
		this.type = type;
	}

	public TagParameterType getType() {
		return type;
	}

}
