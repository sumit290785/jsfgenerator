package jsfgenerator.generation.common.visitors;

import jsfgenerator.generation.view.StaticTagNode;
import jsfgenerator.generation.view.AbstractTagNode;
import jsfgenerator.generation.view.parameters.TagAttribute;

public class IndexVariableVisitor extends AbstractVisitor<AbstractTagNode> {

	private String indexVariableName;

	@Override
	public boolean visit(AbstractTagNode node) {

		if (!(node instanceof StaticTagNode)) {
			return true;
		}

		StaticTagNode stag = (StaticTagNode) node;

		for (TagAttribute attribute : stag.getAttributes()) {
			if (attribute.isIndex()) {
				indexVariableName = attribute.getValue();
				return false;
			}
		}

		return true;
	}

	public boolean indexFound() {
		return indexVariableName != null && !indexVariableName.equals("");
	}

	public String getIndexVariableName() {
		return indexVariableName;
	}

}
