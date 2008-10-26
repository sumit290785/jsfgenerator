package jsfgenerator.inspector.entitymodel.fields;

/**
 * 
 * @author zoltan verebes
 * 
 */
public class TextFieldType extends EntityFieldType {

	public static final int MULTILINE = 1;

	public TextFieldType(int style) {
		this.style = style;
	}

	public boolean isMultiline() {
		return getFlag(0) == 1;
	}
	
}
