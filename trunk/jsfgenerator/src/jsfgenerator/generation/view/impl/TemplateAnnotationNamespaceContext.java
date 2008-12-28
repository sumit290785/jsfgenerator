package jsfgenerator.generation.view.impl;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * 
 * @author zoltan verebes
 * 
 */
public class TemplateAnnotationNamespaceContext implements NamespaceContext {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
	 */
	public String getNamespaceURI(String prefix) {
		if (prefix == null)
			throw new NullPointerException("Null prefix");
		else if ("annotation".equals(prefix)) 
			return ViewTemplateConstants.ANNOTATION_NS_URI;
		else if ("xml".equals(prefix))
			return XMLConstants.XML_NS_URI;
		return XMLConstants.NULL_NS_URI;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
	 */
	public String getPrefix(String namespaceURI) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
	 */
	public Iterator<?> getPrefixes(String namespaceURI) {
		throw new UnsupportedOperationException();
	}

}
