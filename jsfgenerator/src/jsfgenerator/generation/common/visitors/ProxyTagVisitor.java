package jsfgenerator.generation.common.visitors;

import jsfgenerator.generation.view.ProxyTag;
import jsfgenerator.generation.view.TagNode;
import jsfgenerator.generation.view.ProxyTag.ProxyTagType;

public class ProxyTagVisitor extends AbstractVisitor<TagNode> {

	private ProxyTag proxyTag;

	private ProxyTagType type;

	private String name;

	public ProxyTagVisitor(ProxyTagType type) {
		this.type = type;
	}

	public ProxyTagVisitor(String name, ProxyTagType type) {
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
	public boolean visit(TagNode tag) {
		if (tag instanceof ProxyTag && ((ProxyTag) tag).getType().equals(type)
				&& (name == null || (tag.getReferenceName() != null && name.equals(tag.getReferenceName())))) {
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
