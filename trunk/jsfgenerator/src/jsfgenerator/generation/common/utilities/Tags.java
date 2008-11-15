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
	
	public static ProxyTag getProxyTagByType(TagTree tagTree, ProxyTagType type) {
		
		ProxyTagVisitor visitor = new ProxyTagVisitor(type);
		tagTree.apply(visitor);
		
		return visitor.getProxyTag();
	}
	
	public static ProxyTag getProxyTagByType(TagNode node, ProxyTagType type) {
		TagTree tagTree = new TagTree();
		tagTree.addNode(node);
		return getProxyTagByType(tagTree, type);
	}

}
