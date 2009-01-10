package jsfgenerator.generation.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jsfgenerator.generation.common.visitors.AbstractVisitor;

/**
 * Abstract super class of the tag tree and the controller tree. These trees contain information about the views and backing beans of the
 * generation. They are built by the specific tree builders.
 * 
 * @author zoltan verebes
 * 
 */
public abstract class AbstractTree<T extends Node<T>> {

	private List<T> nodes = new ArrayList<T>();

	public void addNode(T node) {
		getNodes().add(node);
	}

	public void addAllNodes(Collection<T> nodes) {
		getNodes().addAll(nodes);
	}

	public void apply(AbstractVisitor<T> visitor) {
		for (T node : getNodes()) {
			node.apply(visitor);
		}
	}
	
	public void apply(AbstractVisitor<T> visitor, List<T> toIgnore) {
		for (T node : getNodes()) {
			node.apply(visitor, toIgnore);
		}
	}
	
	public List<T> getNodes() {
		return nodes;
	}

}
