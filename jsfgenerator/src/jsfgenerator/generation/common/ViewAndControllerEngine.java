package jsfgenerator.generation.common;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import jsfgenerator.entitymodel.EntityModel;
import jsfgenerator.entitymodel.forms.ComplexEntityFormList;
import jsfgenerator.entitymodel.forms.EntityField;
import jsfgenerator.entitymodel.forms.EntityForm;
import jsfgenerator.entitymodel.forms.SimpleEntityForm;
import jsfgenerator.entitymodel.pages.AbstractPageModel;
import jsfgenerator.entitymodel.pages.EntityListPageModel;
import jsfgenerator.entitymodel.pages.EntityPageModel;
import jsfgenerator.generation.common.treebuilders.EntityPageTreeBuilder;
import jsfgenerator.generation.common.visitors.ControllerTreeVisitor;
import jsfgenerator.generation.common.visitors.ExpressionEvaluationTagVisitor;
import jsfgenerator.generation.common.visitors.WriterTagVisitor;
import jsfgenerator.generation.controller.AbstractControllerNodeProvider;
import jsfgenerator.generation.controller.ControllerTree;
import jsfgenerator.generation.view.ITagTreeProvider;
import jsfgenerator.generation.view.TagTree;

/**
 * Singleton class that generates views and controllers by iterating through the
 * entity model and using the tag model to get the right tag information for the
 * entity model elements.
 * 
 * It is a singleton class!
 * 
 * @author zoltan verebes
 * 
 */
public class ViewAndControllerEngine {

	// contains the views and controllers by view id
	private Map<String, ViewAndControllerDTO> views;

	private static ViewAndControllerEngine instance;

	/**
	 * Singleton instance getter
	 * 
	 * @return the only instance of this class
	 */
	public static ViewAndControllerEngine getInstance() {
		if (instance == null) {
			instance = new ViewAndControllerEngine();
		}

		return instance;
	}

	public void generateViewsAndControllers(EntityModel model, ITagTreeProvider tagTreeProvider, AbstractControllerNodeProvider controllerNodeProvider) {

		if (model == null) {
			throw new IllegalArgumentException("Model parameter cannot be null!");
		}

		if (tagTreeProvider == null) {
			throw new IllegalArgumentException("tag tree provider parameter cannot be null");
		}

		if (controllerNodeProvider == null) {
			throw new IllegalArgumentException("controller node provider parameter cannot be null");
		}

		init();
		for (AbstractPageModel pageModel : model.getPageModels()) {
			if (pageModel instanceof EntityPageModel) {
				generateEntityPageViewAndController((EntityPageModel) pageModel, tagTreeProvider, controllerNodeProvider);
			} else if (pageModel instanceof EntityListPageModel) {
				// TODO: list page
			}
		}

	}

	public ViewAndControllerDTO getViewAndControllerDTO(String viewId) {
		if (viewId == null || viewId.equals("")) {
			throw new IllegalArgumentException("View id parameter cannot be null!");
		}

		return views.get(viewId);
	}

	protected ViewAndControllerEngine() {
	}

	protected void init() {
		views = new HashMap<String, ViewAndControllerDTO>();
	}

	protected void generateEntityPageViewAndController(EntityPageModel pageModel, ITagTreeProvider tagTreeProvider,
			AbstractControllerNodeProvider controllerNodeProvider) {

		if (pageModel == null) {
			throw new IllegalArgumentException("Page model cannot be null!");
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		EntityPageTreeBuilder treeBuilder = new EntityPageTreeBuilder(pageModel, tagTreeProvider, controllerNodeProvider);

		for (EntityForm form : pageModel.getForms()) {
			if (form instanceof SimpleEntityForm) {
				SimpleEntityForm simpleForm = (SimpleEntityForm) form;
				treeBuilder.addSimpleForm(simpleForm);

				for (EntityField field : simpleForm.getFields()) {
					treeBuilder.addInputField(simpleForm, field);
				}
			} else if (form instanceof ComplexEntityFormList) {
				ComplexEntityFormList complexForm = (ComplexEntityFormList) form;
				treeBuilder.addComplexFormTagTree(complexForm);
				for (EntityField field : complexForm.getFields()) {
					treeBuilder.addInputField(complexForm, field);
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
		WriterTagVisitor streamWriterVisitor = new WriterTagVisitor(os);
		tagTree.apply(streamWriterVisitor);

		ViewAndControllerDTO viewDTO = new ViewAndControllerDTO(pageModel.getViewId());
		viewDTO.setViewStream(streamWriterVisitor.getOutputStream());
		viewDTO.setViewClass(treeVisitor.getCompilationUnit());
		viewDTO.setControllerClassName(treeVisitor.getRootClassName());
		viewDTO.setViewName(pageModel.getViewId());

		views.put(viewDTO.getViewId(), viewDTO);
	}
}
