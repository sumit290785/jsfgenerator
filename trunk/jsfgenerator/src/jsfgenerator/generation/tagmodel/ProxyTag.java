package jsfgenerator.generation.tagmodel;

import java.util.Set;

import jsfgenerator.generation.tagmodel.parameters.XMLNamespaceParameter;

/**
 * It is a special tag in the tag tree of an element! At generation time other
 * tags are added as children of a proxy tag. Pages have forms hang on a proxy
 * tag, form tag trees have inputs hang on a proxy, etc
 * 
 * @author zoltan verebes
 * 
 */
public class ProxyTag extends Tag {

	public enum ProxyTagType {
		FORM, INPUT
	}

	private ProxyTagType type;

	public ProxyTag(ProxyTagType type) {
		this.type = type;
	}

	public ProxyTagType getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see jsfgenerator.generation.tagmodel.Tag#getXmlNamespaces()
	 */
	@Override
	protected Set<XMLNamespaceParameter> getXmlNamespaces() {
		return null;
	}

}
