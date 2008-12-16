package jsfgenerator.generation.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jsfgenerator.generation.view.parameters.TagAttribute;
import jsfgenerator.generation.view.parameters.XMLNamespaceAttribute;

public class StaticTag extends TagNode {

	// name of the tag
	private String name;

	// parameters of the tag
	private List<TagAttribute> attributess = new ArrayList<TagAttribute>();

	// required namespaces for this tag
	private Set<XMLNamespaceAttribute> xmlNamespaces = new HashSet<XMLNamespaceAttribute>();

	public StaticTag(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Name parameter cannot be null");
		}

		this.name = name;
	}

	/**
	 * 
	 * @return namespace prefix of the tag. it is null if it has no namespace
	 */
	protected String getTagPrefix() {

		if (name == null) {
			throw new NullPointerException("Name of the tag is null!");
		}

		if (name.indexOf(":") == -1) {
			return null;
		}

		return name.substring(0, name.indexOf(":"));
	}

	public String getName() {
		return name;
	}

	public Set<XMLNamespaceAttribute> getXmlNamespaces() {
		return xmlNamespaces;
	}

	public void setAttributes(List<TagAttribute> attributes) {
		this.attributess = attributes;
	}

	public List<TagAttribute> getAttributes() {
		return attributess;
	}

	public void addAttribute(TagAttribute tagAttribute) {
		attributess.add(tagAttribute);
	}
	
}
