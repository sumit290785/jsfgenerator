package jsfgenerator.generation.view.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jsfgenerator.generation.common.treebuilders.ResourceBundleBuilder;
import jsfgenerator.generation.common.utilities.XMLParserUtils;
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
 * It parses the xml file of the view, handles the annotations.xsd and provides view template trees for view tree generation
 * 
 * @author zoltan verebes
 * 
 */
public class ViewTemplateParser implements IViewTemplateProvider {

	private static final Pattern xmlnsPattern = Pattern.compile("^xmlns($|:([A-Za-z0-9]*))$");

	// xml document
	private Document doc;

	private XMLParserUtils xmlParserUtils;

	public ViewTemplateParser(InputStream is) {
		try {
			parseXML(is);
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not parse the input stream!", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.view.IViewTemplateProvider#getEntityListFormTemplateTree()
	 */
	public ViewTemplateTree getEntityListFormTemplateTree() {
		try {
			return getTemplate(ViewTemplateConstants.ENTITY_LIST_FORM);
		} catch (ParserException e) {
			throw new RuntimeException("Entity list form could not be parsed", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.view.IViewTemplateProvider#getEntityPageTemplateTree()
	 */
	public ViewTemplateTree getEntityPageTemplateTree() {
		try {
			return getTemplate(ViewTemplateConstants.ENTITY_PAGE);
		} catch (ParserException e) {
			throw new RuntimeException("Entity page could not be parsed", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.view.IViewTemplateProvider#getEntityFormTemplateTree()
	 */
	public ViewTemplateTree getEntityFormTemplateTree() {
		try {
			return getTemplate(ViewTemplateConstants.ENTITY_FORM);
		} catch (ParserException e) {
			throw new RuntimeException("Entity form could not be parsed", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.view.IViewTemplateProvider#getInputTemplateTree(java.lang.String)
	 */
	public ViewTemplateTree getInputTemplateTree(String inputTagId) {
		try {
			return getTemplate(ViewTemplateConstants.INPUT, inputTagId);
		} catch (ParserException e) {
			throw new RuntimeException("Entity form could not be parsed", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.view.IViewTemplateProvider#getInputTagNames()
	 */
	public List<String> getInputTagNames() {
		List<String> ids = new ArrayList<String>();
		try {
			NodeList inputTagNodeList = xmlParserUtils.getNodes(ViewTemplateConstants.getTemplateXPath(ViewTemplateConstants.INPUT) + "/@name");

			for (int i = 0; i < inputTagNodeList.getLength(); i++) {
				Node inputTagNode = inputTagNodeList.item(i);
				ids.add(inputTagNode.getNodeValue());
			}
		} catch (ParserException e) {
			throw new RuntimeException("Inputs' names could not be parsed", e);
		}

		return ids;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.view.IViewTemplateProvider#getEntityListPageTemplateTree()
	 */
	public ViewTemplateTree getEntityListPageTemplateTree() {
		try {
			return getTemplate(ViewTemplateConstants.ENTITY_LIST_PAGE);
		} catch (ParserException e) {
			throw new RuntimeException("Entity list page could not be parsed", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.view.IViewTemplateProvider#getListColumnDataTemplateTree()
	 */
	public ViewTemplateTree getListColumnDataTemplateTree() {
		try {
			return getTemplate(ViewTemplateConstants.LIST_COLUMN_DATA);
		} catch (ParserException e) {
			throw new RuntimeException("Column data could not be parsed", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.view.IViewTemplateProvider#getListCollectionColumnTemplateTree()
	 */
	public ViewTemplateTree getListCollectionColumnTemplateTree() {
		try {
			return getTemplate(ViewTemplateConstants.LIST_COLLECTION_COLUMN);
		} catch (ParserException e) {
			throw new RuntimeException("Collection column data could not be parsed", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.view.IViewTemplateProvider#getListCollectionColumnDataTemplateTree()
	 */
	public ViewTemplateTree getListCollectionColumnDataTemplateTree() {
		try {
			return getTemplate(ViewTemplateConstants.LIST_COLLECTION_COLUMN_DATA);
		} catch (ParserException e) {
			throw new RuntimeException("Collection column data could not be parsed", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.view.IViewTemplateProvider#getListColumnActionTemplateTree()
	 */
	public ViewTemplateTree getListColumnActionTemplateTree() {
		try {
			return getTemplate(ViewTemplateConstants.LIST_COLUMN_ACTION);
		} catch (ParserException e) {
			throw new RuntimeException("Collection column data could not be parsed", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.view.IViewTemplateProvider#getListColumnHeaderTemplateTree()
	 */
	public ViewTemplateTree getListColumnHeaderTemplateTree() {
		try {
			return getTemplate(ViewTemplateConstants.LIST_COLUMN_HEADER);
		} catch (ParserException e) {
			throw new RuntimeException("Column header could not be parsed", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.view.IViewTemplateProvider#getActionBarTemplateTree()
	 */
	public ViewTemplateTree getActionBarTemplateTree() {
		try {
			return getTemplate(ViewTemplateConstants.ACTION_BAR);
		} catch (ParserException e) {
			throw new RuntimeException("Column header could not be parsed", e);
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
		xmlParserUtils = new XMLParserUtils(doc);
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

		Node tagTreeNode = xmlParserUtils.getNode(exp);
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
				} else if (value.equals(ViewTemplateConstants.LIST_COLUMN_DATA)) {
					return new PlaceholderTagNode(PlaceholderTagNodeType.LIST_COLUMN_DATA);
				} else if (value.equals(ViewTemplateConstants.LIST_COLUMN_HEADER)) {
					return new PlaceholderTagNode(PlaceholderTagNodeType.LIST_COLUMN_HEADER);
				} else if (value.equals(ViewTemplateConstants.LIST_COLLECTION_COLUMN_DATA)) {
					return new PlaceholderTagNode(PlaceholderTagNodeType.LIST_COLLECTION_COLUMN_DATA);
				} else if (value.equals(ViewTemplateConstants.ACTION)) {
					return new PlaceholderTagNode(PlaceholderTagNodeType.ACTION);
				} else if (value.equals(ViewTemplateConstants.ACTION_BAR)) {
					return new PlaceholderTagNode(PlaceholderTagNodeType.ACTION_BAR);
				}
			}

			return null;
		}

		NodeAnnotationProcessor processor = new NodeAnnotationProcessor(node);
		StaticTagNode tag = new StaticTagNode(node.getNodeName());

		/*
		 * root xmlns
		 */
		tag.addAllAttributes(xmlParserUtils.getRootXMLNamespaces());

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

}
