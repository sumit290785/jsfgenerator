package jsfgenerator.generation.common.treebuilders;

import java.util.Collection;
import java.util.List;

import jsfgenerator.generation.common.visitors.PlaceholderTagNodeVisitor;
import jsfgenerator.generation.common.visitors.VarVariableVisitor;
import jsfgenerator.generation.controller.AbstractControllerNodeFactory;
import jsfgenerator.generation.controller.ControllerTree;
import jsfgenerator.generation.view.AbstractTagNode;
import jsfgenerator.generation.view.IViewTemplateProvider;
import jsfgenerator.generation.view.PlaceholderTagNode;
import jsfgenerator.generation.view.StaticTagNode;
import jsfgenerator.generation.view.ViewTemplateTree;
import jsfgenerator.generation.view.PlaceholderTagNode.PlaceholderTagNodeType;

/**
 * Abstract super class for building the view and the controller trees, which are the model of the required classes and xhtml views. The
 * information from the wizard is used to build this up.
 * 
 * @author zoltan verebes
 * 
 */
public abstract class AbstractTreeBuilder {

	protected IViewTemplateProvider templateTreeProvider;

	protected AbstractControllerNodeFactory controllerNodeProvider;

	public AbstractTreeBuilder(IViewTemplateProvider tagTreeProvider, AbstractControllerNodeFactory controllerNodeProvider) {
		this.templateTreeProvider = tagTreeProvider;
		this.controllerNodeProvider = controllerNodeProvider;
	}

	/**
	 * 
	 * @return
	 */
	public abstract ViewTemplateTree getViewTemplateTree();

	/**
	 * 
	 * @return
	 */
	public abstract ControllerTree getControllerTree();

	/**
	 * helper function to find the first appearance of a place holder node with the particular type in the template tree
	 * 
	 * @param view
	 *            template tree source of the search
	 * @param type
	 *            target type of the place holder tag
	 * @return place holder node with the particular type in the tag tree
	 */
	protected PlaceholderTagNode getFirstPlaceholderTagNodeByType(ViewTemplateTree tamplateTree, PlaceholderTagNodeType type) {

		if (tamplateTree == null) {
			throw new IllegalArgumentException("Tag tree parameter cannot be null!");
		}

		if (type == null) {
			throw new IllegalArgumentException("Type parameter cannot be null!");
		}

		PlaceholderTagNodeVisitor visitor = new PlaceholderTagNodeVisitor(type);
		tamplateTree.apply(visitor);

		return visitor.getPlaceholderNode();
	}

	/**
	 * helper static function to find the first appearance of a place holder node with the particular type in the subtree of the node
	 * 
	 * @param node
	 *            its subtree is the source of the search
	 * @param type
	 *            target type of the place holder node
	 * @return place holder node with the particular type in the subtree of the node
	 */
	protected PlaceholderTagNode getFirstPlaceholderTagNodeByType(List<AbstractTagNode> nodes, PlaceholderTagNodeType type) {

		if (nodes == null) {
			throw new IllegalArgumentException("Node parameter cannot be null!");
		}

		if (type == null) {
			throw new IllegalArgumentException("Type parameter cannot be null!");
		}

		ViewTemplateTree tagTree = new ViewTemplateTree();
		tagTree.addAllNodes(nodes);
		return getFirstPlaceholderTagNodeByType(tagTree, type);
	}

	protected String getVarVariableName(AbstractTagNode node) {
		VarVariableVisitor varVariableVisitor = new VarVariableVisitor();
		node.apply(varVariableVisitor);

		if (!varVariableVisitor.variableFound()) {
			return null;
		}

		return varVariableVisitor.getVarVariableName();
	}

	protected String getVarVariableName(Collection<AbstractTagNode> nodes) {
		for (AbstractTagNode node : nodes) {
			String variableName = getVarVariableName(node);
			if (variableName != null) {
				return variableName;
			}
		}

		return null;
	}

	protected String getVarVariableName(ViewTemplateTree tree) {
		return getVarVariableName(tree.getNodes());
	}

	protected StaticTagNode getVariableNode(ViewTemplateTree tree) {
		VarVariableVisitor varVariableVisitor = new VarVariableVisitor();
		tree.apply(varVariableVisitor);

		if (!varVariableVisitor.variableFound()) {
			return null;
		}

		return varVariableVisitor.getVariableNode();
	}

}
