package jsfgenerator.generation.common.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import jsfgenerator.generation.view.impl.ParserException;
import jsfgenerator.generation.view.impl.TemplateAnnotationNamespaceContext;
import jsfgenerator.generation.view.impl.ViewTemplateConstants;
import jsfgenerator.generation.view.parameters.TagAttribute;
import jsfgenerator.generation.view.parameters.XMLNamespaceAttribute;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is a utility class to parse an XML file for nodes from the annotations.xsd XML Schema file
 * 
 * @author zoltan verebes
 * 
 */
public class XMLParserUtils {
	
	private static final Pattern xmlnsPattern = Pattern.compile("^xmlns($|:([A-Za-z0-9]*))$");

	private Document doc;

	private XPathFactory factory = XPathFactory.newInstance();

	public XMLParserUtils(Document doc) {
		if (doc == null) {
			throw new IllegalArgumentException("Document is required parameter");
		}
		this.doc = doc;
	}

	/**
	 * Parses a node list for the passed the xpath expression
	 * 
	 * @param exp
	 *            a valid xpath expression
	 * @return node list for the xpath expression
	 * @throws ParserException
	 */
	public NodeList getNodes(String exp) throws ParserException {
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
	
	public Node getNode(String exp) throws ParserException {
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
	
	public List<TagAttribute> getRootXMLNamespaces() {
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



}
