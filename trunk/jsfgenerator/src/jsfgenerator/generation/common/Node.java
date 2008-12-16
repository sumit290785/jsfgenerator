package jsfgenerator.generation.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jsfgenerator.generation.common.visitors.AbstractVisitor;

@SuppressWarnings("unchecked")
public abstract class Node<T extends Node> {

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
	
	public boolean apply(AbstractVisitor visitor) {
		if (!visitor.visit(this)) {
			visitor.postVisit(this);
			return false;
		}
		
		boolean result = true;
		Iterator<T> it = getChildren().iterator();
		while (result && it.hasNext()) {
			T child = it.next();
			result = child.apply(visitor);
		}

		visitor.postVisit(this);
		return true;
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
