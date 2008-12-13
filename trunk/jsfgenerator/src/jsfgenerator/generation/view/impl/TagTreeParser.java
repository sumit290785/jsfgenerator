package jsfgenerator.generation.view.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
	
	private static final String INPUTTAG_XPATH = "//inputtag";

	// xPATH expression for inputtag domain tags in the xml
	private static final String INPUTTAG_ID_XPATH = INPUTTAG_XPATH + "[@id='{id}']";

	private static final String PROXYTAG = "proxytag";

	private static final String STATICTAG = "statictag";

	private static final String ENTITY_PAGE_TAG_NAME = "entitypage";

	private static final String SIMPLE_FORM_TAG_NAME = "simpleform";

	private static final String COMPLEX_FORM_TAG_NAME = "complexform";

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

	/**
	 * TODO: remove it if i don't use it at the end. it is not used currently
	 * 
	 * @param node
	 * @param styles
	 * @return
	 */
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

	/**
	 * parses the xml from a statictag node down to the bottom of the tree
	 * 
	 * @param rootNode
	 * @return
	 * @throws ParserException
	 */
	protected StaticTag parseStaticTag(Node rootNode) throws ParserException {
		return parseStaticTag(rootNode, null);
	}

	/**
	 * recursive function to parse a static and its inner static and proxy tags
	 * in the xml file
	 * 
	 * @param rootNode
	 * @param styles
	 * @return
	 * @throws ParserException
	 */
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
			attribute = new TagAttribute(keyNode.getTextContent(), valueNode.getTextContent(), TagParameterType.valueOf(typeNode.getTextContent()
					.toUpperCase()));
		} else if (text.equalsIgnoreCase("xmlnamespace")) {
			String valueText = valueNode.getTextContent();
			int delimiterIndex = valueText.indexOf(":");
			attribute = new XMLNamespaceAttribute(valueText.substring(0, delimiterIndex), valueText.substring(delimiterIndex + 1));
		} else if (text.equalsIgnoreCase("template")) {
			attribute = new TemplateAttribute(valueNode.getTextContent());
		}

		return attribute;
	}

	private Node getNode(String exp) throws ParserException {
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
	
	private NodeList getNodes(String exp) throws ParserException  {
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

		return nodes;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jsfgenerator.generation.view.ITagTreeProvider#getInputTag(java.lang.String
	 * )
	 */
	public StaticTag getInputTag(String inputTagId) {
		if (inputTagId == null) {
			throw new IllegalArgumentException("Input tag id parameter cannot be null!");
		}

		String exp = INPUTTAG_ID_XPATH.replace("{id}", inputTagId);
		try {
			Node inputTagNode = getNode(exp);

			NodeList staticTagNodes = inputTagNode.getChildNodes();
			for (int i = 0; i < staticTagNodes.getLength(); i++) {
				Node node = staticTagNodes.item(i);

				if (node.getNodeName().equalsIgnoreCase(STATICTAG)) {
					StaticTag tag = parseStaticTag(node);
					return tag; // return when the matching tag found
				}
			}
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		// TODO
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
		// TODO
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
	
	public TagTree getComplexFormListTagTree() {
		try {
			return getTagTree(COMPLEX_FORM_TAG_NAME);
		} catch (ParserException e) {
			e.printStackTrace();
		}

		return null;
	}

	public List<String> getInputTagIds() {
		List<String> ids = new ArrayList<String>();
		try {
			NodeList inputTagNodeList = getNodes(INPUTTAG_XPATH + "//@id");

			for (int i = 0; i < inputTagNodeList.getLength(); i++) {
				Node inputTagNode = inputTagNodeList.item(i);
				ids.add(inputTagNode.getNodeValue());
			}
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ids;
	}

}