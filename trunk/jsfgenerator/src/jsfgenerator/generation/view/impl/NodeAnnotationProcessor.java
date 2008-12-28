package jsfgenerator.generation.view.impl;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class NodeAnnotationProcessor {

	private Node node;

	private String indexAttributeKey;

	private List<Node> annotations;

	public NodeAnnotationProcessor(Node node) {
		this.node = node;
		process();
	}

	public String getIndexAttributeKey() {
		return indexAttributeKey;
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
		Node indexNode = getAnnotationByName(ViewTemplateConstants.INDEX);
		indexAttributeKey = (indexNode != null && indexNode.getAttributes() != null) ? indexNode.getAttributes().getNamedItem(
				ViewTemplateConstants.INDEX_ATTRIBUTE).getNodeValue() : null;
				
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
		for (Node node : annotations) {
			if (node.getLocalName().equals(name)) {
				return node;
			}
		}

		return null;
	}

	public static boolean isAnnotationNamespaceURI(Node node) {
		return node != null && node.getNamespaceURI() != null
				&& node.getNamespaceURI().equals(ViewTemplateConstants.ANNOTATION_NS_URI);
	}

}
