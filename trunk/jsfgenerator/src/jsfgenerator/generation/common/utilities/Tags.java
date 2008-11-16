package jsfgenerator.generation.common.utilities;

import jsfgenerator.generation.common.visitors.ProxyTagVisitor;
import jsfgenerator.generation.view.ProxyTag;
import jsfgenerator.generation.view.TagNode;
import jsfgenerator.generation.view.TagTree;
import jsfgenerator.generation.view.ProxyTag.ProxyTagType;

/**
 * Utility functions of Tag related things
 * 
 * @author zoltan verebes
 * 
 */
public class Tags {

	/**
	 * helper static function to find the first appearance of a proxy tag with
	 * the particular type in the tag tree
	 * 
	 * @param tagTree source of the search
	 * @param type target type of the proxy tag
	 * @return proxy tag with the praticular type in the tag tree
	 */
	public static ProxyTag getProxyTagByType(TagTree tagTree, ProxyTagType type) {

		if (tagTree == null) {
			throw new IllegalArgumentException("Tag tree parameter cannot be null!");
		}

		if (type == null) {
			throw new IllegalArgumentException("Type parameter cannot be null!");
		}

		ProxyTagVisitor visitor = new ProxyTagVisitor(type);
		tagTree.apply(visitor);

		return visitor.getProxyTag();
	}

	/**
	 * helper static function to find the first appearance of a proxy tag with
	 * the particular type in the subtree of the node
	 * 
	 * @param node its subtree is the source of the search
	 * @param type target type of the proxy tag
	 * @return proxy tag with the praticular type in the subtree of the node
	 */
	public static ProxyTag getProxyTagByType(TagNode node, ProxyTagType type) {

		if (node == null) {
			throw new IllegalArgumentException("Node parameter cannot be null!");
		}

		if (type == null) {
			throw new IllegalArgumentException("Type parameter cannot be null!");
		}

		TagTree tagTree = new TagTree();
		tagTree.addNode(node);
		return getProxyTagByType(tagTree, type);
	}

}
