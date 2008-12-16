package jsfgenerator.generation.common.visitors;

import jsfgenerator.generation.view.StaticTag;
import jsfgenerator.generation.view.TagNode;
import jsfgenerator.generation.view.parameters.TagAttribute;

public class IndexVariableVisitor extends AbstractVisitor<TagNode> {

	private String indexVariableName;

	@Override
	public boolean visit(TagNode node) {

		if (!(node instanceof StaticTag)) {
			return true;
		}

		StaticTag stag = (StaticTag) node;

		for (TagAttribute attribute : stag.getAttributes()) {
			if (attribute.isIndex()) {
				indexVariableName = attribute.getValue();
				return false;
			}
		}

		return true;
	}

	public boolean isIndexFound() {
		return indexVariableName != null && !indexVariableName.equals("");
	}

	public String getIndexVariableName() {
		return indexVariableName;
	}

}
