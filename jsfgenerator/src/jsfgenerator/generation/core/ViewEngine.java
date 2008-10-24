package jsfgenerator.generation.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jsfgenerator.generation.backingbean.naming.BackingBeanNamingFactory;
import jsfgenerator.generation.tagmodel.ITagFactory;
import jsfgenerator.generation.tagmodel.ProxyTag;
import jsfgenerator.generation.tagmodel.StaticTag;
import jsfgenerator.generation.tagmodel.Tag;
import jsfgenerator.generation.tagmodel.ProxyTag.ProxyTagType;
import jsfgenerator.generation.tagmodel.impl.DummyTagFactory;
import jsfgenerator.generation.tagmodel.parameters.TagParameter;
import jsfgenerator.generation.utilities.Tags;
import jsfgenerator.inspector.entitymodel.EntityModel;
import jsfgenerator.inspector.entitymodel.forms.EntityField;
import jsfgenerator.inspector.entitymodel.forms.EntityForm;
import jsfgenerator.inspector.entitymodel.impl.DummyModelEngine;
import jsfgenerator.inspector.entitymodel.pages.EntityListPageModel;
import jsfgenerator.inspector.entitymodel.pages.EntityPageModel;
import jsfgenerator.inspector.entitymodel.pages.PageModel;

/**
 * Generates views by iterating throw the entity model and using the tag model
 * to get the right tag information for the entity model elements.
 * 
 * It is a singleton class!
 * 
 * TODO: replace expression parameters to backing bean EL expression
 * 
 * @author zoltan verebes
 * 
 */
public class ViewEngine {

	private static final String TAB = "\t";
	private static final String NEWLINE = "\n";

	private List<OutputStream> streams = new ArrayList<OutputStream>();

	private static ViewEngine instance;

	protected ViewEngine() {

	}

	/**
	 * Singleton instance getter
	 * 
	 * @return the only instance of this class
	 */
	public static ViewEngine getInstance() {
		if (instance == null) {
			instance = new ViewEngine();
		}

		return instance;
	}

	/**
	 * TODO: check if the file exists
	 * 
	 * @param viewId
	 * @return
	 */
	protected OutputStream createOutputStream(String viewId) {
		return new ByteArrayOutputStream();
	}

	private void appendTabs(StringBuffer buffer, int depth) {
		for (int i = 0; i < depth; i++) {
			buffer.append(TAB);
		}
	}

	private String getTagAsString(StaticTag tag) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(tag.getName());
		for (TagParameter parameter : tag.getParameters()) {
			buffer.append(" ");
			buffer.append(parameter.getName());
			buffer.append("=\"");
			buffer.append(parameter.getValue());
			buffer.append("\"");
			// TODO: change expressions
		}

		return buffer.toString();
	}

	private void writeTag(Tag tag, OutputStream os, int depth) throws IOException {

		if (tag instanceof ProxyTag) {
			for (Tag child : tag.getChildren()) {
				writeTag(child, os, depth);
			}
			return;
		}

		StaticTag staticTag = (StaticTag) tag;

		if (staticTag.isLeaf()) {
			StringBuffer buffer = new StringBuffer();
			appendTabs(buffer, depth);
			buffer.append("<");
			buffer.append(getTagAsString(staticTag));
			buffer.append(" />");
			buffer.append(NEWLINE);
			os.write(buffer.toString().getBytes());
		} else {
			StringBuffer buffer = new StringBuffer();
			appendTabs(buffer, depth);
			buffer.append("<");
			buffer.append(getTagAsString(staticTag));
			buffer.append(">");
			buffer.append(NEWLINE);

			os.write(buffer.toString().getBytes());

			for (Tag child : tag.getChildren()) {
				writeTag(child, os, depth + 1);
			}

			buffer = new StringBuffer();

			appendTabs(buffer, depth);
			buffer.append("</");
			buffer.append(staticTag.getName());
			buffer.append(">");
			buffer.append(NEWLINE);

			os.write(buffer.toString().getBytes());
		}
	}

	protected OutputStream generateEntityPage(EntityPageModel pageModel, ITagFactory tagFactory) {

		if (pageModel == null) {
			throw new IllegalArgumentException("Page model cannot be null!");
		}

		OutputStream os = createOutputStream(pageModel.getViewId());

		Tag tag = tagFactory.getEntityPageTagTree();

		// replace proxy tags - forms
		Tag formProxyTag = Tags.getProxyTagByType(tag, ProxyTagType.FORM);

		if (formProxyTag == null) {
			throw new NullPointerException(
					"FORM proxy tag is not found in the page tag tree! Forms cannot be inserted!");
		}

		BackingBeanNamingFactory namingFactory = BackingBeanNamingFactory.getInstance();
		
		for (EntityForm form : pageModel.getForms()) {
			Tag formTag = tagFactory.getSimpleFormTagTree();
			formProxyTag.addChild(formTag);

			Tag inputProxyTag = Tags.getProxyTagByType(formTag, ProxyTagType.INPUT);
			//List<EntityField> entityFields = Entities.getEntityFields(form.getEntityClass());
			for (EntityField entityField : form.getFields()) {
				StaticTag inputTag = tagFactory.getInputTag(entityField.getType(), namingFactory.getEntityFormNamingContext(pageModel, form, entityField));
				if (inputTag != null) {
					inputProxyTag.addChild(inputTag);
				}
			}
		}

		try {
			writeTag(tag, os, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return os;
	}

	public void generateViews(EntityModel model, ITagFactory tagFactory) {

		if (model == null) {
			throw new IllegalArgumentException("Model parameter cannot be null!");
		}

		if (tagFactory == null) {
			throw new IllegalArgumentException("tag factory parameter cannot be null");
		}

		getStreams().clear();
		for (PageModel pageModel : model.getPageModels()) {
			if (pageModel instanceof EntityPageModel) {

				OutputStream view = generateEntityPage((EntityPageModel) pageModel, tagFactory);
				getStreams().add(view);

			} else if (pageModel instanceof EntityListPageModel) {
				// TODO
			}
		}
	}

	public void setStreams(List<OutputStream> streams) {
		this.streams = streams;
	}

	public List<OutputStream> getStreams() {
		return streams;
	}

	public static void main(String[] args) {
		EntityModel entityModel = (new DummyModelEngine()).createEntityModel();
		ITagFactory tagFactory = new DummyTagFactory();

		ViewEngine engine = ViewEngine.getInstance();
		engine.generateViews(entityModel, tagFactory);

		for (OutputStream os : engine.getStreams()) {
			System.out.println(os);
		}
	}

}
