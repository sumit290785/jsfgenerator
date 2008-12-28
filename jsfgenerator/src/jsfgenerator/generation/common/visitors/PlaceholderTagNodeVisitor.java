package jsfgenerator.generation.common.visitors;

import jsfgenerator.generation.view.AbstractTagNode;
import jsfgenerator.generation.view.PlaceholderTagNode;
import jsfgenerator.generation.view.PlaceholderTagNode.PlaceholderTagNodeType;

public class PlaceholderTagNodeVisitor extends AbstractVisitor<AbstractTagNode> {

	private PlaceholderTagNode placeholderNode;

	private PlaceholderTagNodeType type;

	private String name;

	public PlaceholderTagNodeVisitor(PlaceholderTagNodeType type) {
		this.type = type;
	}

	public PlaceholderTagNodeVisitor(String name, PlaceholderTagNodeType type) {
		this.type = type;
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jsfgenerator.generation.tagmodel.TagVisitor#visit(jsfgenerator.generation
	 * .tagmodel.Tag)
	 */
	@Override
	public boolean visit(AbstractTagNode tag) {
		if (tag instanceof PlaceholderTagNode && ((PlaceholderTagNode) tag).getType().equals(type)
				&& (name == null || (tag.getReferenceName() != null && name.equals(tag.getReferenceName())))) {
			placeholderNode = (PlaceholderTagNode) tag;
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @return first proxy tag in the tree with the specified type
	 */
	public PlaceholderTagNode getPlaceholderNode() {
		return placeholderNode;
	}
}
