package jsfgenerator.generation.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jsfgenerator.generation.view.parameters.TagAttribute;
import jsfgenerator.generation.view.parameters.XMLNamespaceAttribute;

/**
 * 
 * @author zoltan verebes
 *
 */
public class StaticTagNode extends AbstractTagNode {

	// name of the tag
	private String name;

	// parameters of the tag
	private List<TagAttribute> attributes = new ArrayList<TagAttribute>();

	// required namespaces for this tag
	private Set<XMLNamespaceAttribute> xmlNamespaces = new HashSet<XMLNamespaceAttribute>();

	public StaticTagNode(String name) {
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
		this.attributes = attributes;
	}

	public List<TagAttribute> getAttributes() {
		return attributes;
	}

	public void addAttribute(TagAttribute tagAttribute) {
		int i = Collections.binarySearch(attributes, tagAttribute, new Comparator<TagAttribute>() {

			public int compare(TagAttribute tag1, TagAttribute tag2) {
				return tag1.getName().compareTo(tag2.getName());
			}
		});

		if (i < 0) {
			attributes.add(tagAttribute);
		}
	}

	public void addAllAttributes(Collection<TagAttribute> attributes) {
		for (TagAttribute tagAttribute : attributes) {
			addAttribute(tagAttribute);
		}
	}

}
