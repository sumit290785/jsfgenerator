package jsfgenerator.generation.tagmodel.impl;

import jsfgenerator.generation.backingbean.naming.NamingContext;
import jsfgenerator.generation.tagmodel.ITagTreeProvider;
import jsfgenerator.generation.tagmodel.ProxyTag;
import jsfgenerator.generation.tagmodel.StaticTag;
import jsfgenerator.generation.tagmodel.Tag;
import jsfgenerator.generation.tagmodel.TagTree;
import jsfgenerator.generation.tagmodel.ProxyTag.ProxyTagType;
import jsfgenerator.generation.tagmodel.parameters.TagParameter;
import jsfgenerator.generation.tagmodel.parameters.TemplateParameter;
import jsfgenerator.generation.tagmodel.parameters.XMLNamespaceParameter;
import jsfgenerator.inspector.entitymodel.fields.EntityFieldType;
import jsfgenerator.inspector.entitymodel.fields.TextFieldType;

/**
 * Dummy implementation of TagFactory
 * @author zoltan verebes
 *
 */
public class DummyTagFactory implements ITagTreeProvider {

	public TagTree getEntityPageTagTree() {
		
		StaticTag tag = new StaticTag("ui:composition");
		
		tag.addParameter(new XMLNamespaceParameter("h", "http://java.sun.com/jsf/html"));
		tag.addParameter(new XMLNamespaceParameter("ui", "http://java.sun.com/jsf/facelets"));
		tag.addParameter(new TemplateParameter("template.xhtml"));
		
		StaticTag child = new StaticTag("h:outputText");
		child.addParameter(new TagParameter("value", "something"));
		tag.addChild(child);
		
		Tag proxyTag = new ProxyTag(ProxyTagType.FORM);
		tag.addChild(proxyTag);
		
		TagTree tagTree = new TagTree();
		tagTree.addTag(tag);
		
		return tagTree;
	}

	public TagTree getListPageTagTree() {
		// TODO Auto-generated method stub
		return null;
	}

	public TagTree getSimpleFormTagTree() {
		StaticTag tag = new StaticTag("table");
		tag.addChild(new ProxyTag(ProxyTagType.INPUT));
		
		TagTree tagTree = new TagTree();
		tagTree.addTag(tag);
		return tagTree;
	}

	public StaticTag getInputTag(EntityFieldType type, NamingContext namingContext) {
		
		if (type == null) {
			throw new IllegalArgumentException("Type parameter cannot be null!");
		}
		
		if (namingContext == null) {
			throw new IllegalArgumentException("Naming context parameter cannot be null!");
		}
		
		if (type instanceof TextFieldType) {
			TextFieldType textType = (TextFieldType) type;
			if (textType.isMultiline()) {
				StaticTag tag = new StaticTag("h:inputTextArea");
				tag.addParameter(new TagParameter("value", namingContext.getEL("value")));
				tag.addParameter(new TagParameter("cols", "4"));
				return tag;
			} else {
				StaticTag tag = new StaticTag("h:inputText");
				tag.addParameter(new TagParameter("value", namingContext.getEL("value")));
				return tag;
			}
		}
		
		
		return null;
	}


}
