package jsfgenerator.inspector.entitymodel.fields;

public class NumberFieldType extends EntityFieldType {
	
	public static final int INTEGER = 1;
	
	public NumberFieldType(int style) {
		super(style);
	}

	public boolean isInteger() {
		return getFlag(0) == 0;
	}

}
