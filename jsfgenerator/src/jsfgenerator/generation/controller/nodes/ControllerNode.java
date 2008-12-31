package jsfgenerator.generation.controller.nodes;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jsfgenerator.generation.common.Node;

/**
 * Marker abstract class for tree elements of the controller tree
 * 
 * @author zoltan verebes
 * 
 */
public abstract class ControllerNode extends Node<ControllerNode> {

	/**
	 * All the used classes have to be imported into the compilation unit. Used classes are return type, field types, super class,
	 * implemented interfaces, generic parameters of the types
	 * 
	 * @return required imports by this node
	 */
	public Set<String> getRequiredImports() {
		return new HashSet<String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.common.AbstractTree#getNodes()
	 */
	@Override
	public List<ControllerNode> getChildren() {
		List<ControllerNode> children = super.getChildren();
		Collections.sort(children, new Comparator<ControllerNode>() {

			public int compare(ControllerNode node1, ControllerNode node2) {

				if (node1.getClass().equals(node2)) {
					return 0;
				}

				if (node1 instanceof ClassControllerNode || node2 instanceof ClassControllerNode) {
					return (node1 instanceof ClassControllerNode) ? -1 : 1;
				}

				if (node1 instanceof FieldControllerNode || node2 instanceof FieldControllerNode) {
					return (node1 instanceof FieldControllerNode) ? -1 : 1;
				}

				if (node1 instanceof FunctionControllerNode || node2 instanceof FunctionControllerNode) {
					return (node1 instanceof FunctionControllerNode) ? -1 : 1;
				}

				return 0;
			}

		});

		return children;
	}

}
