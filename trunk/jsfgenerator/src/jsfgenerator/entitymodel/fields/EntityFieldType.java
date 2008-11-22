package jsfgenerator.entitymodel.fields;

/**
 * Type of entity field! Subclasses of this abstract class are associated with
 * output tags which are converted into JSF files! Association is known by the
 * implementation of ITagTreeProvider!
 * 
 * Binary representation style supported by style field!
 * 
 * Subclasses are singletons!
 * 
 * @author zoltan verebes
 * 
 */
public abstract class EntityFieldType {
	
	public static final int NONE = 0;
	
	protected int style;
	
	public EntityFieldType(int style) {
		this.style = style;
	}
	
	public EntityFieldType() {
		this.style = NONE;
	}

	public void setStyle(int style) {
		this.style = style;
	}
	
	protected int getFlag(int place) {
		if (place < 0) {
			return 0;
		}
		
		String binaryString = Integer.toBinaryString(style);
		if (binaryString.length() < place) {
			return 0;
		}
		
		return binaryString.charAt(place) == '1' ? 1 : 0;
	}
	
	/**
	 * 
	 * @return style settings of the entity field type
	 */
	public abstract String[] getStyles();
}
