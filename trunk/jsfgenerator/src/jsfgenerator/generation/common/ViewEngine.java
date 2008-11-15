package jsfgenerator.generation.common;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jsfgenerator.generation.common.treebuilders.EntityPageTreeBuilder;
import jsfgenerator.generation.common.visitors.ExpressionEvaluationTagVisitor;
import jsfgenerator.generation.common.visitors.WriterTagVisitor;
import jsfgenerator.generation.view.ITagTreeProvider;
import jsfgenerator.generation.view.TagTree;
import jsfgenerator.inspector.entitymodel.EntityModel;
import jsfgenerator.inspector.entitymodel.fields.EntityField;
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

	protected OutputStream generateEntityPage(EntityPageModel pageModel, ITagTreeProvider tagTreeProvider) {

		if (pageModel == null) {
			throw new IllegalArgumentException("Page model cannot be null!");
		}

		OutputStream os = createOutputStream(pageModel.getName());

		EntityPageTreeBuilder treeBuilder = new EntityPageTreeBuilder(pageModel.getName(), tagTreeProvider, null);

		for (EntityForm form : pageModel.getForms()) {
			if (form instanceof SimpleEntityForm) {
				treeBuilder.addSimpleForm((SimpleEntityForm) form);

				for (EntityField field : form.getFields()) {
					treeBuilder.addInputField((SimpleEntityForm) form, field);
				}
			}
		}

		TagTree tagTree = treeBuilder.getTagTree();
		
		/*
		 * evaluation of expression attributes
		 */
		ExpressionEvaluationTagVisitor expVisitor = new ExpressionEvaluationTagVisitor(tagTree);
		tagTree.apply(expVisitor);

		/*
		 * tag tree is ready to write it out into the output
		 */
		WriterTagVisitor visitor = new WriterTagVisitor(os);
		tagTree.apply(visitor);
		return visitor.getOutputStream();
	}

	public void generateViews(EntityModel model, ITagTreeProvider tagTreeProvider) {

		if (model == null) {
			throw new IllegalArgumentException("Model parameter cannot be null!");
		}

		if (tagTreeProvider == null) {
			throw new IllegalArgumentException("tag tree provider parameter cannot be null");
		}

		getStreams().clear();
		for (PageModel pageModel : model.getPageModels()) {
			if (pageModel instanceof EntityPageModel) {

				OutputStream view = generateEntityPage((EntityPageModel) pageModel, tagTreeProvider);
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

}
