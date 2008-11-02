package jsfgenerator.inspector.entitymodel.fields;

import java.util.ArrayList;
import java.util.List;

public class NumberFieldType extends EntityFieldType {
	
	public static final int INTEGER = 1;
	
	public NumberFieldType(int style) {
		super(style);
	}

	public boolean isInteger() {
		return getFlag(0) == 0;
	}

	/*
	 * (non-Javadoc)
	 * @see jsfgenerator.inspector.entitymodel.fields.EntityFieldType#getStyles()
	 */
	@Override
	public String[] getStyles() {
		List<String> styles = new ArrayList<String>();
		if (getFlag(INTEGER - 1) == 1) {
			styles.add("INTEGER");
		}
		
		if (styles.size() == 0) {
			styles.add("NONE");
		}
		
		return styles.toArray(new String[0]);
	}

}
