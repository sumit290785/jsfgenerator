package jsfgenerator.generation.tagmodel.parameters;

/**
 * Template parameter is a static parameter of a composition! It specifies the
 * template of the page.
 * 
 * @author zoltan verebes
 * 
 */
public class TemplateParameter extends TagParameter {
	
	private static final String TEMPLATE = "template";

	public TemplateParameter(String template) {
		super(TEMPLATE, template);
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
