package jsfgenerator.generation.common.visitors;

import jsfgenerator.generation.view.ProxyTag;
import jsfgenerator.generation.view.StaticTag;
import jsfgenerator.generation.view.TagNode;
import jsfgenerator.generation.view.TagTree;
import jsfgenerator.generation.view.parameters.TagAttribute;
import jsfgenerator.generation.view.parameters.TagAttribute.TagParameterType;

public class ExpressionEvaluationTagVisitor extends AbstractVisitor<TagNode>  {

	private TagTree tagTree;
	
	public ExpressionEvaluationTagVisitor(TagTree tagTree) {
		this.tagTree = tagTree;
	}
	
	@Override
	public boolean visit(TagNode tag) {
		if (tag instanceof ProxyTag) {
			return true;
		}

		StaticTag stag = (StaticTag) tag;
		for (TagAttribute attribute : stag.getAttributes()) {
			if (TagParameterType.EXPRESSION.equals(attribute.getType())) {
				String result = evaluate(attribute.getValue());
				attribute.setValue(result);
			}
		}

		return true;
	}
	
	protected String evaluate(String expression) {
		//TODO: evaluate it with REGEXP
		return expression;
	}

}
