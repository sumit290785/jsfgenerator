package jsfgenerator.generation.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Node<T> {

	protected List<T> children = new ArrayList<T>();

	public List<T> getChildren() {
		return children;
	}

	public void addChild(T childNode) {
		this.children.add(childNode);
	}

	public void addAllChildren(Collection<T> children) {
		for (T node : children) {
			this.addChild(node);
		}
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
