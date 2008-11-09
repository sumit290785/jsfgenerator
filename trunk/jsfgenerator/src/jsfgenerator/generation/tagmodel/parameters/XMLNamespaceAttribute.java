package jsfgenerator.generation.tagmodel.parameters;

/**
 * Model of an xml name space; A name space is defined as a special parameter of
 * a tag! Its name is xmlns and a prefix which is used to refer to the name
 * space in the xhtml jsf view.
 * 
 * @author zoltan verebes
 * 
 */
public class XMLNamespaceAttribute extends TagAttribute {

	private static final String NAME = "xmlns";

	private String prefix;

	public XMLNamespaceAttribute(String prefix, String value) {
		super(NAME + ":" + prefix, value, TagParameterType.STATIC);
		this.prefix = prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	@Override
	public String getName() {
		return NAME + ":" + prefix;
	}

	@Override
	public TagParameterType getType() {
		return TagParameterType.STATIC;
	}

}
