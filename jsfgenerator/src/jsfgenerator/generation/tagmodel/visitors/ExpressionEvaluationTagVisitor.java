package jsfgenerator.generation.tagmodel.visitors;

import jsfgenerator.generation.tagmodel.ProxyTag;
import jsfgenerator.generation.tagmodel.StaticTag;
import jsfgenerator.generation.tagmodel.Tag;
import jsfgenerator.generation.tagmodel.TagTree;
import jsfgenerator.generation.tagmodel.parameters.TagAttribute;
import jsfgenerator.generation.tagmodel.parameters.TagAttribute.TagParameterType;

public class ExpressionEvaluationTagVisitor extends TagVisitor {

	private static class ReferenceNameVisitor extends TagVisitor {
		
		private StringBuffer nameBuffer = new StringBuffer();

		@Override
		public boolean visit(Tag tag) {
			String name = tag.getReferenceName();
			nameBuffer.insert(0, name);
			nameBuffer.append(".");
			return true;
		}
		
		public String getFullReferenceName() {
			return nameBuffer.toString();
		}
		
	}
	
	private TagTree tagTree;
	
	public ExpressionEvaluationTagVisitor(TagTree tagTree) {
		this.tagTree = tagTree;
	}
	
	@Override
	public boolean visit(Tag tag) {
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
