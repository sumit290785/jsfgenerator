package jsfgenerator.generation.tagmodel.visitors;

import jsfgenerator.generation.tagmodel.ProxyTag;
import jsfgenerator.generation.tagmodel.Tag;
import jsfgenerator.generation.tagmodel.ProxyTag.ProxyTagType;

public class ProxyTagVisitor extends TagVisitor {

	private ProxyTag proxyTag;

	private ProxyTagType type;

	public ProxyTagVisitor(ProxyTagType type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jsfgenerator.generation.tagmodel.TagVisitor#visit(jsfgenerator.generation
	 * .tagmodel.Tag)
	 */
	@Override
	public boolean visit(Tag tag) {
		if (tag instanceof ProxyTag && ((ProxyTag) tag).getType().equals(type)) {
			proxyTag = (ProxyTag) tag;
			return false;
		}
		
		return true;
	}

	/**
	 * 
	 * @return first proxy tag in the tree with the specified type
	 */
	public ProxyTag getProxyTag() {
		return proxyTag;
	}
}
