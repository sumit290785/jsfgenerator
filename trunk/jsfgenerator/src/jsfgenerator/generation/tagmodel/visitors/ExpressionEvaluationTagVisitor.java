package jsfgenerator.generation.tagmodel.visitors;

import jsfgenerator.generation.tagmodel.ProxyTag;
import jsfgenerator.generation.tagmodel.StaticTag;
import jsfgenerator.generation.tagmodel.Tag;
import jsfgenerator.generation.tagmodel.parameters.TagAttribute;
import jsfgenerator.generation.tagmodel.parameters.TagAttribute.TagParameterType;

public class ExpressionEvaluationTagVisitor extends TagVisitor {

	@Override
	public boolean visit(Tag tag) {
		if (tag instanceof ProxyTag) {
			return true;
		}

		StaticTag stag = (StaticTag) tag;
		for (TagAttribute attribute : stag.getAttributes()) {
			if (TagParameterType.EXPRESSION.equals(attribute.getType())) {
				System.out.println("expression: " + attribute.getValue());
			}
		}

		return true;
	}

}
