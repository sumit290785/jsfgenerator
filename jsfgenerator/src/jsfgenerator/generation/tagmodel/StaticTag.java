package jsfgenerator.generation.tagmodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jsfgenerator.generation.tagmodel.parameters.TagParameter;
import jsfgenerator.generation.tagmodel.parameters.XMLNamespaceParameter;

public class StaticTag extends Tag {

	// name of the tag
	private String name;
	
	// parameters of the tag
	private List<TagParameter> parameters = new ArrayList<TagParameter>();

	// required namespaces for this tag
	private Set<XMLNamespaceParameter> xmlNamespaces = new HashSet<XMLNamespaceParameter>();

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

	public Set<XMLNamespaceParameter> getXmlNamespaces() {
		return xmlNamespaces;
	}

	public void setParameters(List<TagParameter> parameters) {
		this.parameters = parameters;
	}

	public List<TagParameter> getParameters() {
		return parameters;
	}

	public void addParameter(TagParameter tagParameter) {
		parameters.add(tagParameter);
	}
}
