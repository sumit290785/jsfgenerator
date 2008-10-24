package jsfgenerator.generation.tagmodel.impl;

import jsfgenerator.generation.backingbean.naming.NamingContext;
import jsfgenerator.generation.tagmodel.ITagFactory;
import jsfgenerator.generation.tagmodel.ProxyTag;
import jsfgenerator.generation.tagmodel.StaticTag;
import jsfgenerator.generation.tagmodel.Tag;
import jsfgenerator.generation.tagmodel.ProxyTag.ProxyTagType;
import jsfgenerator.generation.tagmodel.parameters.TagParameter;
import jsfgenerator.generation.tagmodel.parameters.TemplateParameter;
import jsfgenerator.generation.tagmodel.parameters.XMLNamespaceParameter;
import jsfgenerator.inspector.entitymodel.forms.EntityFieldType;

/**
 * Dummy implementation of TagFactory
 * @author zoltan verebes
 *
 */
public class DummyTagFactory implements ITagFactory {

	public Tag getEntityPageTagTree() {
		StaticTag tag = new StaticTag("ui:composition");
		
		tag.addParameter(new XMLNamespaceParameter("h", "http://java.sun.com/jsf/html"));
		tag.addParameter(new XMLNamespaceParameter("ui", "http://java.sun.com/jsf/facelets"));
		tag.addParameter(new TemplateParameter("template.xhtml"));
		
		StaticTag child = new StaticTag("h:outputText");
		child.addParameter(new TagParameter("value", "something"));
		tag.addChild(child);
		
		Tag proxyTag = new ProxyTag(ProxyTagType.FORM);
		tag.addChild(proxyTag);
		
		return tag;
	}

	public Tag getListPageTagTree() {
		// TODO Auto-generated method stub
		return null;
	}

	public Tag getSimpleFormTagTree() {
		StaticTag tag = new StaticTag("table");
		tag.addChild(new ProxyTag(ProxyTagType.INPUT));
		return tag;
	}

	public StaticTag getInputTag(EntityFieldType type, NamingContext namingContext) {
		
		if (String.class.equals(type)) {
			StaticTag tag = new StaticTag("h:inputText");
			tag.addParameter(new TagParameter("value", namingContext.getEL("value")));
			return tag;
		}
		return null;
	}


}
