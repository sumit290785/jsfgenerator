package jsfgenerator.inspector.entitymodel.fields;

/**
 * Type of entity field! Subclasses of this marker interface are associated with
 * output tags which are converted into JSF files! Association is known by the
 * implementation of ITagFactory!
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
		this.style = 0;
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
}
