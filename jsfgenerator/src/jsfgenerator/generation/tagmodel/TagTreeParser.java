package jsfgenerator.generation.tagmodel;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import jsfgenerator.generation.tagmodel.ProxyTag.ProxyTagType;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Parses xml file which contains the tag tree model! It uses DOM, because
 * direct access is required, but it is not a big file, so can be stored in
 * memory
 * 
 * @author zoltan verebes
 * 
 */
public class TagTreeParser {

	private static final String TAGTREE_XPATH = "//tagtree[name='{name}']/";

	private Map<String, TagTree> cache = new HashMap<String, TagTree>();

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

	protected TagTree getTagTree(String name) {
		if (cache.containsKey(name)) {
			return cache.get(name);
		}

		XPath xpath = factory.newXPath();
		String exp = TAGTREE_XPATH.replace("{name}", name);
		XPathExpression expression;
		try {
			expression = xpath.compile(exp);
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException("Name not found in the document: " + name, e);
		}

		Object result;
		try {
			result = expression.evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException("Name not found in the document: " + name, e);
		}

		NodeList nodes = (NodeList) result;

		if (nodes.getLength() == 0) {
			throw new IllegalArgumentException("Tag tree not found with name: " + name);
		}

		if (nodes.getLength() > 1) {
			throw new IllegalArgumentException("Multiple tag trees found! Name: " + name);
		}

		Node tagTreeNode = nodes.item(0);
		NodeList tagNodes = tagTreeNode.getChildNodes();

		TagTree tagTree = new TagTree();

		for (int i = 0; i < tagNodes.getLength(); i++) {
			Node node = tagNodes.item(i);
			NamedNodeMap attributes = node.getAttributes();

			Node nameNode = attributes.getNamedItem("name");
			Node typeNode = attributes.getNamedItem("type");

			Tag tag = null;
			if (typeNode.getTextContent().equals("proxy")) {
				tag = new ProxyTag(ProxyTagType.FORM);
			} else if (typeNode.getTextContent().equals("static")) {
				tag = new StaticTag(nameNode.getTextContent());
			}

			if (tag != null) {
				tagTree.addTag(tag);
			}
		}

		return tagTree;
	}
}
