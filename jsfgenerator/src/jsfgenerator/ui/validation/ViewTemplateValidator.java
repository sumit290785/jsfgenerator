package jsfgenerator.ui.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import jsfgenerator.generation.view.impl.ParserException;
import jsfgenerator.generation.view.impl.TemplateAnnotationNamespaceContext;
import jsfgenerator.generation.view.impl.ViewTemplateConstants;

import org.eclipse.core.resources.IFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ViewTemplateValidator {

	private static class AttributeFilter {
		private String key;
		private String value;

		public AttributeFilter(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}

	private static class ErrorNode {

		private String nodeName;
		private List<AttributeFilter> filters;
		private boolean passed;

		public ErrorNode(String nodeName, List<AttributeFilter> filters, boolean passed) {
			this.nodeName = nodeName;
			this.filters = filters;
			this.setPassed(passed);
		}

		public String getNodeName() {
			return nodeName;
		}

		public List<AttributeFilter> getFilters() {
			return filters;
		}

		public void setPassed(boolean passed) {
			this.passed = passed;
		}

		public boolean isPassed() {
			return passed;
		}
	}

	private InputStream is;

	private List<String> messages = new ArrayList<String>();

	private Document doc;

	private XPathFactory factory;
	
	public ViewTemplateValidator(IFile file) {

		if (file == null) {
			throw new IllegalArgumentException("File cannot be null");
		}

		File f = file.getLocation().toFile();

		try {
			is = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		factory = XPathFactory.newInstance();
	}

	public void validate() {
		try {
			parseXML(is);
		} catch (Exception e) {
			messages.add("Could not parse the selected file");
			return;
		}

		try {
			validatePlaceHolder(ViewTemplateConstants.ENTITY_PAGE);
		} catch (ParserException e) {
			messages.add("Could not parse the selected file");
			return;
		}
	}

	public List<String> getMessages() {
		return messages;
	}

	public boolean validationPassed() {
		return messages.size() == 0;
	}

	private void validatePlaceHolder(String name) throws ParserException {
		String exp = ViewTemplateConstants.getTemplateXPath(name);
		Node rootNode = getNode(exp);

		List<ErrorNode> whiteList = Collections.emptyList();
		List<ErrorNode> blackList = Collections.emptyList();
		if (name.equals(ViewTemplateConstants.ENTITY_PAGE)) {
			whiteList = createPlaceHolderElement(false, ViewTemplateConstants.ENTITY_FORM, ViewTemplateConstants.ENTITY_LIST_FORM);
			blackList = createPlaceHolderElement(true, ViewTemplateConstants.LIST_COLUMN_DATA,
					ViewTemplateConstants.LIST_COLLECTION_COLUMN, ViewTemplateConstants.LIST_COLLECTION_COLUMN_DATA,
					ViewTemplateConstants.LIST_COLUMN_HEADER);
		}

		findErrors(rootNode, whiteList, blackList);
		for (ErrorNode errorNode : whiteList) {
			if (!errorNode.isPassed()) {
				messages.add(getNodeName(errorNode) + ": not found in node " + getNodeName(rootNode));
			}
		}

		for (ErrorNode errorNode : blackList) {
			if (!errorNode.isPassed()) {
				messages.add(getNodeName(errorNode) + ": should not be in node " + getNodeName(rootNode));
			}
		}
	}

	private List<ErrorNode> createPlaceHolderElement(boolean b, String... elements) {
		List<ErrorNode> nodes = new ArrayList<ErrorNode>();
		for (String element : elements) {
			nodes.add(new ErrorNode(ViewTemplateConstants.PLACE_HOLDER, Arrays.asList(new AttributeFilter(
					ViewTemplateConstants.PLACE_HOLDER_FOR, element)), b));
		}

		return nodes;
	}

	private void findErrors(Node rootNode, List<ErrorNode> whiteList, List<ErrorNode> blackList) {
		NodeList children = rootNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);

			// check the white list
			for (ErrorNode error : whiteList) {
				if (!error.isPassed() && match(child, error)) {
					error.setPassed(true);
				}
			}

			// check the black list
			for (ErrorNode error : blackList) {
				if (match(child, error)) {
					error.setPassed(false);
				}
			}

			findErrors(child, whiteList, blackList);
		}
	}

	private boolean match(Node node, ErrorNode error) {
		if (node.getLocalName() == null || !node.getLocalName().equals(error.getNodeName())) {
			return false;
		}

		for (AttributeFilter filter : error.getFilters()) {
			Node attr = node.getAttributes().getNamedItem(filter.getKey());

			if (!attr.getNodeValue().equals(filter.getValue())) {
				return false;
			}
		}

		return true;
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

	private String getNodeName(ErrorNode error) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(error.getNodeName());

		if (error.getFilters().size() != 0) {
			buffer.append("[");
		}

		for (int i = 0; i < error.getFilters().size(); i++) {
			AttributeFilter filter = error.getFilters().get(i);
			buffer.append(filter.getKey());
			buffer.append("=");
			buffer.append(filter.getValue());

			if (i != error.getFilters().size() - 1) {
				buffer.append(", ");
			}
		}

		if (error.getFilters().size() != 0) {
			buffer.append("]");
		}

		return buffer.toString();
	}

	private String getNodeName(Node node) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(node.getNodeName());

		if (node.getAttributes().getLength() != 0) {
			buffer.append("[");
		}

		for (int i = 0; i < node.getAttributes().getLength(); i++) {
			Node attr = node.getAttributes().item(i);
			buffer.append(attr.getNodeName());
			buffer.append("=");
			buffer.append(attr.getNodeValue());

			if (i != node.getAttributes().getLength() - 1) {
				buffer.append(", ");
			}
		}

		if (node.getAttributes().getLength() != 0) {
			buffer.append("]");
		}

		return buffer.toString();
	}
}
