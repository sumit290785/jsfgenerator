package jsfgenerator.generation.view;

import java.util.HashSet;
import java.util.Set;

import jsfgenerator.generation.common.Node;
import jsfgenerator.generation.view.parameters.XMLNamespaceAttribute;

/**
 * Models the information about a tag in the view! A tag has a name, some
 * parameters and children! It stores information about the required namespaces
 * which have to be imported at the beginning of the file!
 * 
 * @author zoltan verebes
 * 
 */
public abstract class AbstractTagNode extends Node<AbstractTagNode> {

	private String referenceName;

	// required xml namespaces of the tag
	protected abstract Set<XMLNamespaceAttribute> getXmlNamespaces();

	/**
	 * Collects required namespaces recursively in the tree
	 * 
	 * @return required namespace parameters for this tag and its children
	 */
	public Set<XMLNamespaceAttribute> getRequiredNamespaces() {
		Set<XMLNamespaceAttribute> requiredNamespaces = new HashSet<XMLNamespaceAttribute>();

		for (AbstractTagNode child : children) {
			requiredNamespaces.addAll(child.getRequiredNamespaces());
		}

		if (getXmlNamespaces() != null) {
			requiredNamespaces.addAll(getXmlNamespaces());
		}

		return requiredNamespaces;
	}

	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	public String getReferenceName() {
		return referenceName;
	}

}
