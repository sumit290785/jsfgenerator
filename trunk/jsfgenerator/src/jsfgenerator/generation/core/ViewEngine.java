package jsfgenerator.generation.core;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jsfgenerator.generation.backingbean.naming.BackingBeanNamingFactory;
import jsfgenerator.generation.tagmodel.ITagTreeProvider;
import jsfgenerator.generation.tagmodel.StaticTag;
import jsfgenerator.generation.tagmodel.Tag;
import jsfgenerator.generation.tagmodel.TagTree;
import jsfgenerator.generation.tagmodel.ProxyTag.ProxyTagType;
import jsfgenerator.generation.tagmodel.visitors.WriterTagVisitor;
import jsfgenerator.generation.utilities.Tags;
import jsfgenerator.inspector.entitymodel.EntityModel;
import jsfgenerator.inspector.entitymodel.fields.EntityField;
import jsfgenerator.inspector.entitymodel.forms.ComplexEntityFormList;
import jsfgenerator.inspector.entitymodel.forms.EntityForm;
import jsfgenerator.inspector.entitymodel.forms.SimpleEntityForm;
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

	protected OutputStream generateEntityPage(EntityPageModel pageModel, ITagTreeProvider tagFactory) {

		if (pageModel == null) {
			throw new IllegalArgumentException("Page model cannot be null!");
		}

		OutputStream os = createOutputStream(pageModel.getViewId());

		TagTree tagTree = tagFactory.getEntityPageTagTree();

		// replace proxy tags - forms
		Tag formProxyTag = Tags.getProxyTagByType(tagTree, ProxyTagType.FORM);

		if (formProxyTag == null) {
			throw new NullPointerException(
					"FORM proxy tag is not found in the page tag tree! Forms cannot be inserted!");
		}

		BackingBeanNamingFactory namingFactory = BackingBeanNamingFactory.getInstance();

		for (EntityForm form : pageModel.getForms()) {

			if (form instanceof SimpleEntityForm) {

				TagTree formTagTree = tagFactory.getSimpleFormTagTree();
				formProxyTag.addAllChildren(formTagTree.getTags());

				Tag inputProxyTag = Tags.getProxyTagByType(formTagTree, ProxyTagType.INPUT);

				if (inputProxyTag == null) {
					throw new NullPointerException(
							"INPUT proxy tag is not found in the form tag tree! Inputs cannot be inserted!");
				}

				for (EntityField entityField : form.getFields()) {
					StaticTag inputTag = tagFactory.getInputTag(entityField.getType(), namingFactory
							.getEntityFormNamingContext(pageModel, form, entityField));
					if (inputTag != null) {
						inputProxyTag.addChild(inputTag);
					}
				}
			} else if (form instanceof ComplexEntityFormList) {
				throw new UnsupportedOperationException("Complex forms are not supported, yet");
			}
		}

		WriterTagVisitor visitor = new WriterTagVisitor(os);
		tagTree.apply(visitor);
		return visitor.getOutputStream();
	}

	public void generateViews(EntityModel model, ITagTreeProvider tagFactory) {

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

		// test
		for (OutputStream os : streams) {
			System.out.println(os.toString());
		}
	}

	public void setStreams(List<OutputStream> streams) {
		this.streams = streams;
	}

	public List<OutputStream> getStreams() {
		return streams;
	}

}
