package jsfgenerator.generation.tagmodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jsfgenerator.generation.tagmodel.parameters.XMLNamespaceParameter;

/**
 * Models the information about a tag in the view! A tag has a name, some
 * parameters and children! It stores information about the required namespaces
 * which have to be imported at the beginning of the file!
 * 
 * @author zoltan verebes
 * 
 */
public abstract class Tag {

	// tag children of tag
	protected List<Tag> children = new ArrayList<Tag>();
	
	// required xml namespaces of the tag
	protected abstract Set<XMLNamespaceParameter> getXmlNamespaces();

	/**
	 * Collects required namespaces recursively in the tree
	 * 
	 * @return required namespace parameters for this tag and its children
	 */
	public Set<XMLNamespaceParameter> getRequiredNamespaces() {
		Set<XMLNamespaceParameter> requiredNamespaces = new HashSet<XMLNamespaceParameter>();

		for (Tag child : getChildren()) {
			requiredNamespaces.addAll(child.getRequiredNamespaces());
		}

		if (getXmlNamespaces() != null) {
			requiredNamespaces.addAll(getXmlNamespaces());	
		}

		return requiredNamespaces;
	}

	public void setChildren(List<Tag> children) {
		this.children = children;
	}

	public List<Tag> getChildren() {
		return children;
	}

	public void addChild(Tag childTag) {
		children.add(childTag);
	}

	/**
	 * a tree element is leaf it has no children
	 * 
	 * @return true when the tag is leaf
	 */
	public boolean isLeaf() {
		return children.size() == 0;
	}

}
