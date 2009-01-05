package jsfgenerator.generation.view;

import java.util.Set;

import jsfgenerator.generation.view.parameters.XMLNamespaceAttribute;

/**
 * It is a special tag in the tag tree of an element! At generation time other tags are added as children of a proxy tag. Pages have forms
 * hang on a proxy tag, form tag trees have inputs hang on a proxy, etc
 * 
 * @author zoltan verebes
 * 
 */
public class PlaceholderTagNode extends AbstractTagNode {

	public enum PlaceholderTagNodeType {
		ENTITY_FORM, ENTITY_LIST_FORM, INPUT, LIST_COLUMN_DATA, LIST_COLUMN_HEADER, LIST_COLLECTION_COLUMN_DATA, ACTION;
	}

	private PlaceholderTagNodeType type;

	public PlaceholderTagNode(PlaceholderTagNodeType type) {
		this.type = type;
	}

	public PlaceholderTagNodeType getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.tagmodel.Tag#getXmlNamespaces()
	 */
	@Override
	protected Set<XMLNamespaceAttribute> getXmlNamespaces() {
		return null;
	}

}
