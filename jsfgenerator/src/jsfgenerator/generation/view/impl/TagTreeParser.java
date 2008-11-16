package jsfgenerator.generation.view.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import jsfgenerator.generation.view.ITagTreeProvider;
import jsfgenerator.generation.view.ProxyTag;
import jsfgenerator.generation.view.StaticTag;
import jsfgenerator.generation.view.TagNode;
import jsfgenerator.generation.view.TagTree;
import jsfgenerator.generation.view.ProxyTag.ProxyTagType;
import jsfgenerator.generation.view.parameters.TagAttribute;
import jsfgenerator.generation.view.parameters.TemplateAttribute;
import jsfgenerator.generation.view.parameters.XMLNamespaceAttribute;
import jsfgenerator.generation.view.parameters.TagAttribute.TagParameterType;
import jsfgenerator.inspector.entitymodel.fields.EntityFieldType;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Parses xml file which contains the tag tree model! It uses DOM, because
 * direct access is required, but it is not a big file, so it is not a horror to
 * store it in the working memory
 * 
 * TODO: describe the xml file in the comment when there is a final version
 * 
 * @author zoltan verebes
 * 
 */
public class TagTreeParser implements ITagTreeProvider {

	private static final String ATTRIBUTE = "attribute";

	// xPATH expression for tagtree domain tags in the xml
	private static final String TAGTREE_XPATH = "//tagtree[@id='{id}']";

	// xPATH expression for inputtag domain tags in the xml
	private static final String INPUTTAG_XPATH = "//inputtag[@class='{class}']";

	private static final String PROXYTAG = "proxytag";

	private static final String STATICTAG = "statictag";

	private static final String ENTITY_PAGE_TAG_NAME = "entitypage";

	private static final String SIMPLE_FORM_TAG_NAME = "simpleform";

	// cache for the tag trees due to store already parsed tag trees
	private Map<String, TagTree> tagTreeCache = new HashMap<String, TagTree>();

	// xml document
	private Document doc;

	private XPathFactory factory;

	public TagTreeParser(InputStream is) {
		try {
			parseXML(is);
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not parse the input stream!", e);
		}

		factory = XPathFactory.newInstance();
	}

	protected void parseXML(InputStream is) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = factory.newDocumentBuilder();

		doc = builder.parse(is);
	}

	private boolean stylesMatch(Node node, String[] styles) {
		if (styles == null) {
			return true;
		}

		Node styleNode = node.getAttributes().getNamedItem("styles");
		String styleText;
		if (styleNode == null) {
			styleText = "NONE";
		} else {
			styleText = styleNode.getNodeValue();
		}

		String[] filters = styleText.split(",");
		List<String> f = Arrays.asList(filters);

		return f.containsAll(Arrays.asList(styles));
	}

	protected StaticTag parseStaticTag(Node rootNode) throws ParserException {
		return parseStaticTag(rootNode, null);
	}

	protected StaticTag parseStaticTag(Node rootNode, String[] styles) throws ParserException {
		NamedNodeMap attributes = rootNode.getAttributes();

		Node nameNode = attributes.getNamedItem("name");

		StaticTag tag = new StaticTag(nameNode.getTextContent());

		NodeList children = rootNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);

			if (node.getNodeName().equalsIgnoreCase(STATICTAG) && stylesMatch(node, styles)) {
				TagNode childTag = parseStaticTag(node, styles);
				tag.addChild(childTag);
			} else if (node.getNodeName().equalsIgnoreCase(PROXYTAG)) {
				TagNode childTag = parseProxyTag(node);
				tag.addChild(childTag);
			} else if (node.getNodeName().equalsIgnoreCase(ATTRIBUTE)) {
				if (!(tag instanceof StaticTag)) {
					throw new IllegalArgumentException("Attribute is legal only for static tags");
				}

				StaticTag stag = (StaticTag) tag;
				TagAttribute attribute = parseTagAttribute(node);
				if (attribute != null) {
					stag.addAttribute(attribute);
				}
			}
		}

		return tag;
	}

	protected ProxyTag parseProxyTag(Node rootNode) throws ParserException {
		NamedNodeMap attributes = rootNode.getAttributes();
		Node typeNode = attributes.getNamedItem("type");

		if (typeNode == null || typeNode.getTextContent().equals("")) {
			throw new ParserException("Illegal node type!");
		}

		// try to find the type
		ProxyTagType type = ProxyTagType.valueOf(typeNode.getTextContent());
		ProxyTag tag = new ProxyTag(type);

		// there is not any subtree to be parsed for proxy tags
		return tag;
	}

	protected TagAttribute parseTagAttribute(Node node) throws ParserException {
		NamedNodeMap attributes = node.getAttributes();
		Node typeNode = attributes.getNamedItem("type");
		Node keyNode = attributes.getNamedItem("key");
		Node valueNode = attributes.getNamedItem("value");

		if (typeNode == null || typeNode.getTextContent().equals("")) {
			throw new IllegalArgumentException("Illegal type of node!");
		}

		String text = typeNode.getTextContent();
		TagAttribute attribute = null;
		if (text.equalsIgnoreCase("static") || text.equalsIgnoreCase("expression")) {
			attribute = new TagAttribute(keyNode.getTextContent(), valueNode.getTextContent(), TagParameterType
					.valueOf(typeNode.getTextContent().toUpperCase()));
		} else if (text.equalsIgnoreCase("xmlnamespace")) {
			String valueText = valueNode.getTextContent();
			int delimiterIndex = valueText.indexOf(":");
			attribute = new XMLNamespaceAttribute(valueText.substring(0, delimiterIndex), valueText
					.substring(delimiterIndex + 1));
		} else if (text.equalsIgnoreCase("template")) {
			attribute = new TemplateAttribute(valueNode.getTextContent());
		}

		return attribute;
	}

	private Node getNode(String exp) throws ParserException {
		XPath xpath = factory.newXPath();
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
			e.printStackTrace();
			throw new ParserException("Node not found in the document. Expression: " + exp, e);
		}

		NodeList nodes = (NodeList) result;

		if (nodes.getLength() == 0) {
			throw new ParserException("Node not found for expression: " + exp);
		}

		if (nodes.getLength() > 1) {
			throw new ParserException("Multiple nodes found! Expression: " + exp);
		}

		Node node = nodes.item(0);

		return node;
	}

	protected TagTree getTagTree(String id) throws ParserException {
		if (tagTreeCache.containsKey(id)) {
			return tagTreeCache.get(id);
		}

		String exp = TAGTREE_XPATH.replace("{id}", id);

		Node tagTreeNode = getNode(exp);
		NodeList tagNodes = tagTreeNode.getChildNodes();

		TagTree tagTree = new TagTree();

		for (int i = 0; i < tagNodes.getLength(); i++) {
			Node node = tagNodes.item(i);

			TagNode tag = null;
			if (node.getNodeName().equalsIgnoreCase(STATICTAG)) {
				tag = parseStaticTag(node);
			} else if (node.getNodeName().equalsIgnoreCase(PROXYTAG)) {
				tag = parseProxyTag(node);
			}

			if (tag != null) {
				tagTree.addNode(tag);
			}
		}

		return tagTree;
	}

	protected StaticTag getInputTag(String className, String[] styles) throws ParserException {
		if (className == null || className.equals("")) {
			throw new IllegalArgumentException("Class name parameter must not be null!");
		}

		String exp = INPUTTAG_XPATH.replace("{class}", className);
		Node inputTagNode = getNode(exp);

		NodeList staticTagNodes = inputTagNode.getChildNodes();
		for (int i = 0; i < staticTagNodes.getLength(); i++) {
			Node node = staticTagNodes.item(i);

			if (node.getNodeName().equalsIgnoreCase(STATICTAG) && stylesMatch(node, styles)) {
				// TODO: filter for style parameters
				StaticTag tag = parseStaticTag(node, styles);
				return tag; // return when the matching tag found
			}
		}

		return null; // tag not found for the style parameters and the class
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jsfgenerator.generation.tagmodel.ITagTreeProvider#getEntityPageTagTree()
	 */
	public TagTree getEntityPageTagTree() {
		try {
			return getTagTree(ENTITY_PAGE_TAG_NAME);
		} catch (ParserException e) {
			e.printStackTrace();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jsfgenerator.generation.tagmodel.ITagTreeProvider#getListPageTagTree()
	 */
	public TagTree getListPageTagTree() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jsfgenerator.generation.tagmodel.ITagTreeProvider#getSimpleFormTagTree()
	 */
	public TagTree getSimpleFormTagTree() {
		try {
			return getTagTree(SIMPLE_FORM_TAG_NAME);
		} catch (ParserException e) {
			e.printStackTrace();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jsfgenerator.generation.tagmodel.ITagTreeProvider#getInputTag(jsfgenerator
	 * .inspector.entitymodel.fields.EntityFieldType)
	 */
	public StaticTag getInputTag(EntityFieldType type) {
		if (type == null) {
			throw new IllegalArgumentException("Type parameter cannot be null!");
		}

		StaticTag tag = null;
		try {
			tag = getInputTag(type.getClass().getCanonicalName(), type.getStyles());
		} catch (ParserException e) {
			e.printStackTrace();
		}

		return tag;
	}
}
