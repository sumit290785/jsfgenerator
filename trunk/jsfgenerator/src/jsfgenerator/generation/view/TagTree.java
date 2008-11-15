package jsfgenerator.generation.view;

import jsfgenerator.generation.common.AbstractTree;

/**
 * represents a tag tree! Visitors can be accepted on every element of the tree
 * 
 * @author zoltan verebes
 * 
 */
public class TagTree extends AbstractTree<TagNode> {

	/**
	 * reference name is applied for all of the top level tags of the tag tree
	 * 
	 * @param referenceName
	 */
	public void applyReferenceName(String referenceName) {
		for (TagNode tag : getNodes()) {
			tag.setReferenceName(referenceName);
		}
	}

}
