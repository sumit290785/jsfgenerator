package jsfgenerator.generation.tagmodel.visitors;

import jsfgenerator.generation.tagmodel.Tag;

/**
 * Abstract visitor class for tag trees!
 * 
 * @author zoltan verebes
 * 
 */
public abstract class TagVisitor {

	/**
	 * called when a tag is visited in the tree
	 * 
	 * @param tag
	 */
	public abstract boolean visit(Tag tag);

	public void postVisit(Tag tag) {}
}
