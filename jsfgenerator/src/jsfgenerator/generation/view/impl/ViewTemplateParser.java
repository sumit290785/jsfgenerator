package jsfgenerator.generation.view.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import jsfgenerator.generation.common.treebuilders.ResourceBundleBuilder;
import jsfgenerator.generation.common.visitors.ReferenceNameEvaluatorVisitor.ExpressionType;
import jsfgenerator.generation.view.AbstractTagNode;
import jsfgenerator.generation.view.IViewTemplateProvider;
import jsfgenerator.generation.view.PlaceholderTagNode;
import jsfgenerator.generation.view.StaticTagNode;
import jsfgenerator.generation.view.ViewTemplateTree;
import jsfgenerator.generation.view.PlaceholderTagNode.PlaceholderTagNodeType;
import jsfgenerator.generation.view.parameters.TagAttribute;
import jsfgenerator.generation.view.parameters.TemplateAttribute;
import jsfgenerator.generation.view.parameters.XMLNamespaceAttribute;
import jsfgenerator.generation.view.parameters.TagAttribute.TagParameterType;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author zoltan verebes
 * 
 */
public class ViewTemplateParser implements IViewTemplateProvider {

	private static final Pattern xmlnsPattern = Pattern.compile("^xmlns($|:([A-Za-z0-9]*))$");

	// xml document
	private Document doc;

	private XPathFactory factory;

	public ViewTemplateParser(InputStream is) {
		try {
			parseXML(is);
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not parse the input stream!", e);
		}

		factory = XPathFactory.newInstance();
	}

	public ViewTemplateTree getEntityListFormTemplateTree() {
		try {
			return getTemplate(ViewTemplateConstants.ENTITY_LIST_FORM);
		} catch (ParserException e) {
			throw new RuntimeException("Entity list form could not be parsed", e);
		}
	}

	public ViewTemplateTree getEntityPageTemplateTree() {
		try {
			return getTemplate(ViewTemplateConstants.ENTITY_PAGE);
		} catch (ParserException e) {
			throw new RuntimeException("Entity page could not be parsed", e);
		}
	}

	public ViewTemplateTree getEntityFormTemplateTree() {
		try {
			return getTemplate(ViewTemplateConstants.ENTITY_FORM);
		} catch (ParserException e) {
			throw new RuntimeException("Entity form could not be parsed", e);
		}
	}

	public ViewTemplateTree getInputTemplateTree(String inputTagId) {
		try {
			return getTemplate(ViewTemplateConstants.INPUT, inputTagId);
		} catch (ParserException e) {
			throw new RuntimeException("Entity form could not be parsed", e);
		}
	}

	public List<String> getInputTagNames() {
		List<String> ids = new ArrayList<String>();
		try {
			NodeList inputTagNodeList = getNodes(ViewTemplateConstants.getTemplateXPath(ViewTemplateConstants.INPUT) + "/@name");

			for (int i = 0; i < inputTagNodeList.getLength(); i++) {
				Node inputTagNode = inputTagNodeList.item(i);
				ids.add(inputTagNode.getNodeValue());
			}
		} catch (ParserException e) {
			throw new RuntimeException("Inputs' names could not be parsed", e);
		}

		return ids;
	}
	
	public ViewTemplateTree getEntityListPageTemplateTree() {
		try {
			return getTemplate(ViewTemplateConstants.ENTITY_LIST_PAGE);
		} catch (ParserException e) {
			throw new RuntimeException("Entity list page could not be parsed", e);
		}
	}
	
	public ViewTemplateTree getEntityListElementTemplateTree() {
		try {
			return getTemplate(ViewTemplateConstants.ENTITY_LIST_ELEMENT);
		} catch (ParserException e) {
			throw new RuntimeException("Entity list page could not be parsed", e);
		}
	}

	/**
	 * parses the document with DOM
	 * 
	 * @param is
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	protected void parseXML(InputStream is) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = factory.newDocumentBuilder();

		doc = builder.parse(is);
	}

	protected ViewTemplateTree getTemplate(String... args) throws ParserException {

		if (args.length == 0) {
			return null;
		}

		String exp;
		if (args.length == 1) {
			exp = ViewTemplateConstants.getTemplateXPath(args[0]);
		} else {
			exp = ViewTemplateConstants.getTemplateXPath(args[0], args[1]);
		}

		Node tagTreeNode = getNode(exp);
		NodeList tagNodes = tagTreeNode.getChildNodes();

		ViewTemplateTree templateTree = new ViewTemplateTree();

		for (int i = 0; i < tagNodes.getLength(); i++) {
			AbstractTagNode tag = parseNode(tagNodes.item(i));

			if (tag != null) {
				templateTree.addNode(tag);
			}
		}

		return templateTree;
	}

	protected AbstractTagNode parseNode(Node node) throws ParserException {
		if (node.getNodeType() == Node.TEXT_NODE) {
			return null;
		}

		if (NodeAnnotationProcessor.isAnnotationNamespaceURI(node)) {
			if (node.getLocalName().equals(ViewTemplateConstants.PLACE_HOLDER)) {
				Node attribute = node.getAttributes().getNamedItem(ViewTemplateConstants.PLACE_HOLDER_FOR);
				String value = attribute.getNodeValue();
				if (value.equals(ViewTemplateConstants.ENTITY_FORM)) {
					return new PlaceholderTagNode(PlaceholderTagNodeType.ENTITY_FORM);
				} else if (value.equals(ViewTemplateConstants.ENTITY_LIST_FORM)) {
					return new PlaceholderTagNode(PlaceholderTagNodeType.ENTITY_LIST_FORM);
				} else if (value.equals(ViewTemplateConstants.INPUT)) {
					return new PlaceholderTagNode(PlaceholderTagNodeType.INPUT);
				}
			}
			return null;
		}

		NodeAnnotationProcessor processor = new NodeAnnotationProcessor(node);
		StaticTagNode tag = new StaticTagNode(node.getNodeName());

		/*
		 * root xmlns
		 */
		tag.addAllAttributes(getRootXMLNamespaces());

		for (int i = 0; node.getAttributes() != null && i < node.getAttributes().getLength(); i++) {
			TagAttribute attribute = getTagAttribute(node.getAttributes().item(i), processor);
			if (attribute != null) {
				tag.addAttribute(attribute);
			}
		}

		/*
		 * expressions
		 */
		for (Node expressionNode : processor.getExpressionNodes()) {
			Node typeNode = expressionNode.getAttributes().getNamedItem(ViewTemplateConstants.EXPRESSION_TYPE);
			Node forNode = expressionNode.getAttributes().getNamedItem(ViewTemplateConstants.EXPRESSION_FOR);

			ExpressionType type = null;
			try {
				type = ExpressionType.getTypeByName(typeNode.getTextContent());
			} catch (Exception e) {
				throw new ParserException("Invalid expression type: " + typeNode.getTextContent(), e);
			}

			TagAttribute expressionAttribute = new TagAttribute(forNode.getTextContent(), type.toString(),
					TagParameterType.EXPRESSION, false);
			tag.addAttribute(expressionAttribute);
		}

		/*
		 * change messages for annotated tags
		 */
		for (TagAttribute attribute : tag.getAttributes()) {
			if (processor.isAnnotationMessage(attribute.getName())) {
				ResourceBundleBuilder resourceBuilder = ResourceBundleBuilder.getInstance();
				resourceBuilder.addKey(attribute.getValue().toLowerCase());
				attribute.setValue("#{" + resourceBuilder.getTranslateMethodInvocation(attribute.getValue().toLowerCase()) + "}");
			}
		}

		NodeList children = node.getChildNodes();
		for (int i = 0; children != null && i < children.getLength(); i++) {
			AbstractTagNode childTag = parseNode(children.item(i));
			if (childTag != null) {
				tag.addChild(childTag);
			}
		}

		return tag;
	}

	protected TagAttribute getTagAttribute(Node node, NodeAnnotationProcessor annotationProcessor) {
		if (node.getNodeType() != Node.ATTRIBUTE_NODE) {
			return null;
		}

		String key = node.getNodeName();
		String value = node.getNodeValue();

		TagAttribute attribute;
		Matcher matcher = xmlnsPattern.matcher(key);
		if (matcher.matches()) {
			attribute = new XMLNamespaceAttribute(matcher.group(2), value);
		} else if ("template".equalsIgnoreCase(key)) {
			attribute = new TemplateAttribute(value);
		} else {
			boolean varVariable = annotationProcessor.getVarAttributeKey() != null
					&& annotationProcessor.getVarAttributeKey().equals(key);
			attribute = new TagAttribute(key, value, TagParameterType.STATIC, varVariable);
		}

		return attribute;
	}

	protected Node getNode(String exp) throws ParserException {
		NodeList nodes = getNodes(exp);

		if (nodes.getLength() == 0) {
			throw new ParserException("Node not found for expression: " + exp);
		}

		if (nodes.getLength() > 1) {
			throw new ParserException("Multiple nodes found! Expression: " + exp);
		}

		Node node = nodes.item(0);

		return node;
	}

	protected List<TagAttribute> getRootXMLNamespaces() {
		Node root;
		try {
			root = getNode(ViewTemplateConstants.ROOT_XPATH);
		} catch (ParserException e) {
			return Collections.emptyList();
		}

		List<TagAttribute> attributes = new ArrayList<TagAttribute>();
		for (int i = 0; root.getAttributes() != null && i < root.getAttributes().getLength(); i++) {
			Node attributeNode = root.getAttributes().item(i);

			if (!ViewTemplateConstants.ANNOTATION_NS_URI.equals(attributeNode.getNodeValue())) {
				Matcher matcher = xmlnsPattern.matcher(attributeNode.getNodeName());
				if (matcher.matches()) {
					attributes.add(new XMLNamespaceAttribute(matcher.group(2), attributeNode.getNodeValue()));
				}
			}
		}

		return attributes;
	}

	protected NodeList getNodes(String exp) throws ParserException {
		XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(new TemplateAnnotationNamespaceContext());
		XPathExpression expression;
		try {
			expression = xpath.compile(exp);
		} catch (XPathExpressionException e) {
			throw new ParserException("Node not found in the document. Expression: " + exp, e);
		}

		Object result;
		try {
			result = expression.evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new ParserException("Node not found in the document. Expression: " + exp, e);
		}

		NodeList nodes = (NodeList) result;

		return nodes;
	}
	

	public static void main(String[] args) {
		InputStream is = ViewTemplateParser.class.getResourceAsStream("viewtemplate.xml");
		ViewTemplateParser parser = new ViewTemplateParser(is);

		try {
			parser.getTemplate("entityListForm");
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}


}
