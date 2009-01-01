package jsfgenerator.generation.common.visitors;

import jsfgenerator.generation.view.StaticTagNode;
import jsfgenerator.generation.view.AbstractTagNode;
import jsfgenerator.generation.view.parameters.TagAttribute;

public class VarVariableVisitor extends AbstractVisitor<AbstractTagNode> {

	private String varVariableName;

	@Override
	public boolean visit(AbstractTagNode node) {

		if (!(node instanceof StaticTagNode)) {
			return true;
		}

		StaticTagNode stag = (StaticTagNode) node;

		for (TagAttribute attribute : stag.getAttributes()) {
			if (attribute.isVarVariable()) {
				varVariableName = attribute.getValue();
				return false;
			}
		}

		return true;
	}

	public boolean variableFound() {
		return varVariableName != null && !varVariableName.equals("");
	}

	public String getVarVariableName() {
		return varVariableName;
	}

}
