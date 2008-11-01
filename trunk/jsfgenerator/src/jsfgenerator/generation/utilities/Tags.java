package jsfgenerator.generation.utilities;

import jsfgenerator.generation.tagmodel.ProxyTagVisitor;
import jsfgenerator.generation.tagmodel.Tag;
import jsfgenerator.generation.tagmodel.TagTree;
import jsfgenerator.generation.tagmodel.ProxyTag.ProxyTagType;

/**
 * Utility functions of Tag related things
 * 
 * @author zoltan verebes
 * 
 */
public class Tags {
	
	public static Tag getProxyTagByType(TagTree tagTree, ProxyTagType type) {
		
		ProxyTagVisitor visitor = new ProxyTagVisitor(type);
		tagTree.apply(visitor);
		
		return visitor.getProxyTag();
	}

}
