package jsfgenerator.generation.view.parameters;

/**
 * Template parameter is a static parameter of a composition! It specifies the
 * template of the page.
 * 
 * @author zoltan verebes
 * 
 */
public class TemplateAttribute extends TagAttribute {
	
	private static final String TEMPLATE = "template";

	public TemplateAttribute(String template) {
		super(TEMPLATE, template, TagParameterType.STATIC, false);
	}

	@Override
	public TagParameterType getType() {
		return TagParameterType.STATIC;
	}

	@Override
	public String getName() {
		return TEMPLATE;
	}

}
