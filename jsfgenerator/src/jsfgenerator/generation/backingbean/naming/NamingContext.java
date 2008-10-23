package jsfgenerator.generation.backingbean.naming;

/**
 * Common interface to get backing bean naming when the tag is generated! It
 * binds the names for parameter names! Context is up to to the page and the
 * page's part!
 * 
 * @author zoltan verebes
 * 
 */
public abstract class NamingContext {

	protected final static String EL_BEGIN = "#{";

	protected final static String EL_END = "}";
	
	protected final static String SEPARATOR = ".";

	/**
	 * Backing bean reference for the parameter!
	 * 
	 * @param paramName
	 * @return
	 */
	public String getEL(String paramName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(EL_BEGIN);
		buffer.append(getReference(paramName));
		buffer.append(EL_END);

		return buffer.toString();
	}

	/**
	 * Reference for the expression language expression
	 * 
	 * @param paramName
	 * @return
	 */
	protected abstract String getReference(String paramName);

}
