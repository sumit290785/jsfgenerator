package jsfgenerator.generation.tagmodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jsfgenerator.generation.tagmodel.visitors.TagVisitor;

/**
 * represents a tag tree! Visitors can be accepted on every element of the tree
 * 
 * @author zoltan verebes
 * 
 */
public class TagTree {

	private List<Tag> tags = new ArrayList<Tag>();

	public void addTag(Tag tag) {
		tags.add(tag);
	}

	/**
	 * Visitor's visit method is invoked for all of the nodes of the tag tree.
	 * The order of the tag nodes is the order of inorder the depth search.
	 * 
	 * @param visitor
	 */
	public void apply(TagVisitor visitor) {
		for (Tag tag : tags) {
			apply(tag, visitor);
		}
	}

	/**
	 * Visitor visits all of the parents of the tag from the tag up to the root
	 * element of the tree or until visitor's visit function's result is true.
	 * First it visits the closer tags above itself. If vistor returns with
	 * false it stops
	 * 
	 * @param visitor
	 * @param tag
	 */
	public void applyParents(Tag tag, TagVisitor visitor) {
		Tag parent = tag.getParent();
		if (parent == null) {
			return;
		}
		
		if (visitor.visit(parent)) {
			applyParents(parent, visitor);
		}
	}

	/**
	 * reference name is applied for all of the top level tags of the tag tree
	 * 
	 * @param referenceName
	 */
	public void applyReferenceName(String referenceName) {
		for (Tag tag : tags) {
			tag.setReferenceName(referenceName);
		}
	}

	protected boolean apply(Tag tag, TagVisitor visitor) {
		if (!visitor.visit(tag)) {
			visitor.postVisit(tag);
			return false;
		}

		boolean result = true;
		Iterator<Tag> it = tag.getChildren().iterator();
		while (result && it.hasNext()) {
			Tag child = it.next();
			result = apply(child, visitor);
		}

		visitor.postVisit(tag);
		return true;
	}

	public List<Tag> getTags() {
		return tags;
	}
}
