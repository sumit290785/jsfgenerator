package jsfgenerator.generation.common.visitors;

import jsfgenerator.generation.common.Node;

/**
 * Abstract visitor for abstract trees.
 * 
 * @author zoltan verebes
 * 
 * @param <T>
 */
public abstract class AbstractVisitor<T extends Node<T>> {

	/**
	 * called when a node is visited in the tree
	 * 
	 * @param tag
	 */
	public abstract boolean visit(T node);

	/**
	 * called when a node is left
	 * 
	 * @param node
	 */
	public void postVisit(T node) {
	}
}
