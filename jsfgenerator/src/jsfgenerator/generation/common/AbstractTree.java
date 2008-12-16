package jsfgenerator.generation.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jsfgenerator.generation.common.visitors.AbstractVisitor;

/**
 * Abstract super class of the tag tree and the controller tree. These trees
 * contain information about the views and backing beans of the generation. They
 * are built by the specific tree builders.
 * 
 * @author zoltan verebes
 * 
 */
public abstract class AbstractTree<T extends Node<T>> {

	private List<T> nodes = new ArrayList<T>();

	public void addNode(T node) {
		getNodes().add(node);
	}

	public void apply(AbstractVisitor<T> visitor) {
		for (T node : getNodes()) {
			node.apply(visitor);
			//apply(node, visitor);
		}
	}

	private boolean apply(T node, AbstractVisitor<T> visitor) {
		if (!visitor.visit(node)) {
			visitor.postVisit(node);
			return false;
		}

		boolean result = true;
		Iterator<T> it = node.getChildren().iterator();
		while (result && it.hasNext()) {
			T child = it.next();
			result = apply(child, visitor);
		}

		visitor.postVisit(node);
		return true;
	}
	
	public List<T> getNodes() {
		return nodes;
	}

}
