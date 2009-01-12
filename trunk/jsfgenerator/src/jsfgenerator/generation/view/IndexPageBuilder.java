package jsfgenerator.generation.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import jsfgenerator.generation.common.treebuilders.ResourceBundleBuilder;

public class IndexPageBuilder {

	private static IndexPageBuilder instance;

	private StringBuffer buffer;
	
	private boolean finished = false;

	private IndexPageBuilder() {
		clear();
	}

	public static IndexPageBuilder getInstance() {
		if (instance == null) {
			instance = new IndexPageBuilder();
		}

		return instance;
	}

	public void clear() {
		finished = false;
		
		ResourceBundleBuilder.getInstance().addKey("welcome");		
		buffer = new StringBuffer();
		buffer.append("<ui:composition");
		addXmlns(null, "http://www.w3.org/1999/xhtml");
		addXmlns("ui", "http://java.sun.com/jsf/facelets");
		addXmlns("h", "http://java.sun.com/jsf/html");
		addXmlns("jsfgen", "http://www.jsfgen.com/tags/jsfgen");
		buffer.append(" ");
		buffer.append(" template=\"layout/template.xhtml\"");
		buffer.append(" >");
		buffer.append("\n");
		buffer.append("\t<ul>");
	}

	public void addView(String viewId) {
		
		if (finished) {
			throw new RuntimeException("Index page has been finished!");
		}
		
		buffer.append("\n\t<li>\n");
		buffer.append("\t\t<h:outputLink value=\"");
		buffer.append(viewId);
		buffer.append(".jsf");
		buffer.append("\">");
		buffer.append(viewId);
		buffer.append("</h:outputLink>\n");
		buffer.append("\t</li>");
	}

	public void finish() {
		if (finished) {
			throw new RuntimeException("Index page has been finished!");
		}
		
		buffer.append("\t</ul>");
		buffer.append("\n");
		buffer.append("</ui:composition>");
		buffer.append("\n");
		
		finished = true;
	}

	public InputStream getContent() {
		return new ByteArrayInputStream(buffer.toString().getBytes());
	}

	private void addXmlns(String key, String value) {
		buffer.append(" xmlns");
		if (key != null) {
			buffer.append(":");
			buffer.append(key);
		}

		buffer.append("=\"");
		buffer.append(value);
		buffer.append("\"");
		buffer.append("\n");
	}

}
