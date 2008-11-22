package jsfgenerator.entitymodel.fields;

import java.util.ArrayList;
import java.util.List;

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

	/*
	 * (non-Javadoc)
	 * @see jsfgenerator.inspector.entitymodel.fields.EntityFieldType#getStyles()
	 */
	@Override
	public String[] getStyles() {
		List<String> styles = new ArrayList<String>();
		if (getFlag(MULTILINE - 1) == 1) {
			styles.add("MULTILINE");
		}
		
		if (styles.size() == 0) {
			styles.add("NONE");
		}
		
		return styles.toArray(new String[0]);
	}
	
}
