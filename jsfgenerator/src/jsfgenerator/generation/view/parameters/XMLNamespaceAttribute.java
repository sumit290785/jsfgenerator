package jsfgenerator.generation.view.parameters;

/**
 * Model of an xml name space; A name space is defined as a special parameter of a tag! Its name is xmlns and a prefix which is used to
 * refer to the name space in the xhtml jsf view.
 * 
 * @author zoltan verebes
 * 
 */
public class XMLNamespaceAttribute extends TagAttribute {

	private static final String NAME = "xmlns";

	public XMLNamespaceAttribute(String prefix, String value) {
		super((prefix == null ? NAME : (NAME + ":" + prefix)), value, TagParameterType.STATIC, false);
	}

}
