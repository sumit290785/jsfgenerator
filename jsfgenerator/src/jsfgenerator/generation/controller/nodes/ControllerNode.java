package jsfgenerator.generation.controller.nodes;

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
	 * All the used classes have to be imported into the compilation unit. Used
	 * classes are return type, field types, super class, implemented
	 * interfaces, generic parameters of the types
	 * 
	 * @return required imports by this node
	 */
	public abstract Set<String> getRequiredImports();

}
