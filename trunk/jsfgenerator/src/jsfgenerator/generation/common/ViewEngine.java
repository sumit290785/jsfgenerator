package jsfgenerator.generation.common;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import jsfgenerator.entitymodel.EntityModel;
import jsfgenerator.entitymodel.fields.EntityField;
import jsfgenerator.entitymodel.forms.EntityForm;
import jsfgenerator.entitymodel.forms.SimpleEntityForm;
import jsfgenerator.entitymodel.pages.EntityListPageModel;
import jsfgenerator.entitymodel.pages.EntityPageModel;
import jsfgenerator.entitymodel.pages.PageModel;
import jsfgenerator.generation.common.treebuilders.EntityPageTreeBuilder;
import jsfgenerator.generation.common.visitors.ControllerTreeVisitor;
import jsfgenerator.generation.common.visitors.ExpressionEvaluationTagVisitor;
import jsfgenerator.generation.common.visitors.WriterTagVisitor;
import jsfgenerator.generation.controller.ControllerTree;
import jsfgenerator.generation.controller.nodes.IControllerNodeProvider;
import jsfgenerator.generation.view.ITagTreeProvider;
import jsfgenerator.generation.view.TagTree;

import org.eclipse.jdt.core.dom.CompilationUnit;

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

	private Map<String, ByteArrayOutputStream> views;
	private Map<String, CompilationUnit> controllers;

	private static ViewEngine instance;

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

	public void generateViewsAndControllers(EntityModel model, ITagTreeProvider tagTreeProvider,
			IControllerNodeProvider controllerNodeProvider) {

		if (model == null) {
			throw new IllegalArgumentException("Model parameter cannot be null!");
		}

		if (tagTreeProvider == null) {
			throw new IllegalArgumentException("tag tree provider parameter cannot be null");
		}

		init();
		for (PageModel pageModel : model.getPageModels()) {
			if (pageModel instanceof EntityPageModel) {
				generateEntityPageViewAndController((EntityPageModel) pageModel, tagTreeProvider,
						controllerNodeProvider);
			} else if (pageModel instanceof EntityListPageModel) {
				// TODO: list page
			}
		}

	}

	/**
	 * 
	 * @param viewId
	 * @return
	 */
	public ByteArrayOutputStream getView(String viewId) {
		if (viewId == null || viewId.equals("")) {
			throw new IllegalArgumentException("View id parameter cannot be null!");
		}

		return views.get(viewId);
	}

	/**
	 * 
	 * @param viewId
	 * @return
	 */
	public CompilationUnit getController(String viewId) {
		if (viewId == null || viewId.equals("")) {
			throw new IllegalArgumentException("View id parameter cannot be null!");
		}

		return controllers.get(viewId);
	}

	protected ViewEngine() {
	}

	protected void init() {
		views = new HashMap<String, ByteArrayOutputStream>();
		controllers = new HashMap<String, CompilationUnit>();
	}

	protected void generateEntityPageViewAndController(EntityPageModel pageModel, ITagTreeProvider tagTreeProvider,
			IControllerNodeProvider controllerNodeProvider) {

		if (pageModel == null) {
			throw new IllegalArgumentException("Page model cannot be null!");
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		// TODO
		EntityPageTreeBuilder treeBuilder = new EntityPageTreeBuilder(pageModel.getName(), tagTreeProvider,
				controllerNodeProvider);

		for (EntityForm form : pageModel.getForms()) {
			if (form instanceof SimpleEntityForm) {
				treeBuilder.addSimpleForm((SimpleEntityForm) form);

				for (EntityField field : form.getFields()) {
					treeBuilder.addInputField((SimpleEntityForm) form, field);
				}
			}
		}

		TagTree tagTree = treeBuilder.getTagTree();
		ControllerTree controllerTree = treeBuilder.getControllerTree();

		/*
		 * generate controller java class with ControllerTreeVisitor class
		 */
		ControllerTreeVisitor treeVisitor = new ControllerTreeVisitor(controllerTree);
		controllerTree.apply(treeVisitor);

		/*
		 * TODO: evaluation of expression attributes
		 */
		ExpressionEvaluationTagVisitor expVisitor = new ExpressionEvaluationTagVisitor(tagTree);
		tagTree.apply(expVisitor);

		/*
		 * tag tree is ready to write it out into the output
		 */
		WriterTagVisitor visitor = new WriterTagVisitor(os);
		tagTree.apply(visitor);

		views.put(pageModel.getName(), visitor.getOutputStream());
		controllers.put(pageModel.getName(), treeVisitor.getCompilationUnit());
	}

}
