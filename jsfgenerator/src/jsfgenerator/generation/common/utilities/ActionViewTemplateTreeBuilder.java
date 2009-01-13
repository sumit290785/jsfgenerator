package jsfgenerator.generation.common.utilities;

import jsfgenerator.generation.common.treebuilders.ResourceBundleBuilder;
import jsfgenerator.generation.common.visitors.ReferenceNameEvaluatorVisitor.ExpressionType;
import jsfgenerator.generation.view.StaticTagNode;
import jsfgenerator.generation.view.parameters.TagAttribute;
import jsfgenerator.generation.view.parameters.TagAttribute.TagParameterType;

/**
 * This class has static functions to create standard view trees for actions like new, add, remove, delete
 * 
 * @author zoltan verebes
 * 
 */
public class ActionViewTemplateTreeBuilder {

	public static StaticTagNode getSaveActionNode() {
		StaticTagNode node = new StaticTagNode("h:commandLink");
		ResourceBundleBuilder.getInstance().addKey("save");
		node.addAttribute(new TagAttribute("value", "#{jsfgen:translate('save')}", TagParameterType.STATIC, false));
		node.addAttribute(new TagAttribute("action", ExpressionType.SAVE.toString(), TagParameterType.EXPRESSION, false));

		return node;
	}

	public static StaticTagNode getRemoveActionNode() {
		StaticTagNode node = new StaticTagNode("h:commandLink");
		ResourceBundleBuilder.getInstance().addKey("remove");
		node.addAttribute(new TagAttribute("value", "#{jsfgen:translate('remove')}", TagParameterType.STATIC, false));
		node.addAttribute(new TagAttribute("action", ExpressionType.REMOVE.toString(), TagParameterType.EXPRESSION, false));

		return node;
	}
	
	public static StaticTagNode getAddActionNode() {
		StaticTagNode node = new StaticTagNode("h:commandLink");
		ResourceBundleBuilder.getInstance().addKey("add");
		node.addAttribute(new TagAttribute("value", "#{jsfgen:translate('add')}", TagParameterType.STATIC, false));
		node.addAttribute(new TagAttribute("action", ExpressionType.ADD.toString(), TagParameterType.EXPRESSION, false));

		return node;
	}

	private static StaticTagNode getOutputLinkActionNode(String entityPageViewId, String text) {
		StaticTagNode node = new StaticTagNode("h:outputLink");
		node.addAttribute(new TagAttribute("value", entityPageViewId + ".jsf", TagParameterType.STATIC, false));

		StaticTagNode textNode = new StaticTagNode("h:outputText");
		ResourceBundleBuilder.getInstance().addKey(text);
		textNode.addAttribute(new TagAttribute("value", "#{jsfgen:translate('" + text + "')}", TagParameterType.STATIC, false));
		node.addChild(textNode);

		return node;
	}

	public static StaticTagNode getNewAtionNode(String entityPageViewId) {
		return getOutputLinkActionNode(entityPageViewId, "new");
	}

	public static StaticTagNode getSelectAtionNode(String entityPageViewId, String namespace, String idFieldName) {
		StaticTagNode paramNode = new StaticTagNode("f:param");
		paramNode.addAttribute(new TagAttribute("name", "entityId", TagParameterType.STATIC, false));
		paramNode.addAttribute(new TagAttribute("value", "#{" + namespace + "." + idFieldName + "}", TagParameterType.STATIC,
				false));
		StaticTagNode node = getOutputLinkActionNode(entityPageViewId, "select");
		node.addChild(paramNode);

		return node;
	}

}
