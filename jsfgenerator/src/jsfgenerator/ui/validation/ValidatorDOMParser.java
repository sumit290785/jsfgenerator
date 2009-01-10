package jsfgenerator.ui.validation;

import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;

public class ValidatorDOMParser extends DOMParser {

	public static final String LINE_NUMBER_KEY = "startLine";

	private XMLLocator locator;

	@Override
	public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
		super.startElement(element, attributes, augs);

		Node node = null;
		try {
			node = (Node) this.getProperty(CURRENT_ELEMENT_NODE);
		} catch (org.xml.sax.SAXException ex) {
			System.err.println("except" + ex);
			;
		}
		if (node != null) {
			node.setUserData(LINE_NUMBER_KEY, String.valueOf(locator.getLineNumber()), null); // Save location String into node
		}
	}

	@Override
	public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs)
			throws XNIException {
		super.startDocument(locator, encoding, namespaceContext, augs);
		this.locator = locator;
		Node node = null;
		try {
			node = (Node) this.getProperty(CURRENT_ELEMENT_NODE);
		} catch (org.xml.sax.SAXException ex) {
			System.err.println("except" + ex);
		}

		if (node != null) {
			node.setUserData("startLine", String.valueOf(locator.getLineNumber()), null);
		}
	}

	public static Document createDocument(InputStream is) throws SAXException, IOException {
		ValidatorDOMParser parser = new ValidatorDOMParser();
		parser.setFeature(DEFER_NODE_EXPANSION, false);
		parser.parse(new InputSource(is));
		Document doc = parser.getDocument();
		return doc;
	}

}
