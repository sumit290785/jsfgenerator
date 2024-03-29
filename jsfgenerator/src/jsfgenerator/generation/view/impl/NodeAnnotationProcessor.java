package jsfgenerator.generation.view.impl;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class NodeAnnotationProcessor {

	private Node node;

	private String varAttributeKey;

	private List<Node> annotations;

	private List<Node> messageNodes;

	public NodeAnnotationProcessor(Node node) {
		this.node = node;
		process();
	}

	public String getVarAttributeKey() {
		return varAttributeKey;
	}

	public List<Node> getExpressionNodes() {
		List<Node> expressions = new ArrayList<Node>();

		for (Node node : annotations) {
			if (node.getLocalName().equals(ViewTemplateConstants.EXPRESSION)) {
				expressions.add(node);
			}
		}

		return expressions;
	}

	protected void process() {
		annotations = getAnnotations();

		/*
		 * index attribute
		 */
		Node varNode = getAnnotationByName(ViewTemplateConstants.VARIABLE);
		varAttributeKey = (varNode != null && varNode.getAttributes() != null) ? varNode.getAttributes().getNamedItem(
				ViewTemplateConstants.VAR_ATTRIBUTE).getNodeValue() : null;

		/*
		 * process messages
		 */
		messageNodes = getAnnotationsByName(ViewTemplateConstants.MESSAGE);
	}

	protected List<Node> getAnnotations() {
		List<Node> annotations = new ArrayList<Node>();
		Node prevNode = node.getPreviousSibling();
		while (prevNode != null && (prevNode.getNodeType() == Node.TEXT_NODE || isAnnotationNamespaceURI(prevNode))) {
			if (prevNode.getNodeType() != Node.TEXT_NODE) {
				annotations.add(prevNode);
			}
			prevNode = prevNode.getPreviousSibling();
		}

		return annotations;
	}

	protected Node getAnnotationByName(String name) {
		List<Node> nodes = getAnnotationsByName(name);

		if (nodes.size() == 0) {
			return null;
		}

		return nodes.get(0);
	}

	protected List<Node> getAnnotationsByName(String name) {
		List<Node> annotations = new ArrayList<Node>();
		for (Node node : this.annotations) {
			if (node.getLocalName().equals(name)) {
				annotations.add(node);
			}
		}

		return annotations;
	}

	public static boolean isAnnotationNamespaceURI(Node node) {
		return node != null && node.getNamespaceURI() != null
				&& node.getNamespaceURI().equals(ViewTemplateConstants.ANNOTATION_NS_URI);
	}

	public boolean isAnnotationMessage(String attributeName) {
		for (Node node : messageNodes) {
			return node.getAttributes().getNamedItem("attribute").getNodeValue().equals(attributeName);
		}

		return false;
	}
}
