package jsfgenerator.generation.common;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import jsfgenerator.entitymodel.EntityModel;
import jsfgenerator.entitymodel.pageelements.AbstractEntityForm;
import jsfgenerator.entitymodel.pageelements.ColumnModel;
import jsfgenerator.entitymodel.pageelements.EntityField;
import jsfgenerator.entitymodel.pageelements.EntityForm;
import jsfgenerator.entitymodel.pageelements.EntityListForm;
import jsfgenerator.entitymodel.pages.AbstractPageModel;
import jsfgenerator.entitymodel.pages.EntityListPageModel;
import jsfgenerator.entitymodel.pages.EntityPageModel;
import jsfgenerator.generation.common.treebuilders.AbstractTreeBuilder;
import jsfgenerator.generation.common.treebuilders.EntityListPageTreeBuilder;
import jsfgenerator.generation.common.treebuilders.EntityPageTreeBuilder;
import jsfgenerator.generation.common.visitors.ControllerTreeVisitor;
import jsfgenerator.generation.common.visitors.WriterTagVisitor;
import jsfgenerator.generation.controller.AbstractControllerNodeFactory;
import jsfgenerator.generation.controller.ControllerTree;
import jsfgenerator.generation.view.IViewTemplateProvider;
import jsfgenerator.generation.view.IndexPageBuilder;
import jsfgenerator.generation.view.ViewTemplateTree;

/**
 * Singleton class that generates views and controllers by iterating through the entity model and using the tag model to get the right tag
 * information for the entity model elements.
 * 
 * It is a singleton class!
 * 
 * @author zoltan verebes
 * 
 */
public final class ViewAndControllerEngine {

	// contains the views and controllers by view id
	private Map<String, ViewAndControllerDTO> views;

	private static ViewAndControllerEngine instance;

	protected ViewAndControllerEngine() {
	}

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

	protected void init() {
		views = new HashMap<String, ViewAndControllerDTO>();
		IndexPageBuilder.getInstance().clear();
	}

	public void generateViewsAndControllers(EntityModel model, IViewTemplateProvider tagTreeProvider,
			AbstractControllerNodeFactory controllerNodeProvider) {

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
			generatePageViewAndController(pageModel, tagTreeProvider, controllerNodeProvider);
		}

	}

	protected void generatePageViewAndController(AbstractPageModel pageModel, IViewTemplateProvider tagTreeProvider,
			AbstractControllerNodeFactory controllerNodeProvider) {

		if (pageModel == null) {
			throw new IllegalArgumentException("Page model cannot be null!");
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		AbstractTreeBuilder treeBuilder;
		if (pageModel instanceof EntityPageModel) {
			treeBuilder = generateEntityPageViewAndController((EntityPageModel) pageModel, tagTreeProvider,
					controllerNodeProvider);
		} else {
			treeBuilder = generateListPageViewAndController((EntityListPageModel) pageModel, tagTreeProvider,
					controllerNodeProvider);
		}

		ControllerTree controllerTree = treeBuilder.getControllerTree();

		ViewAndControllerDTO viewDTO = new ViewAndControllerDTO(pageModel.getViewId());

		/*
		 * generate controller java class with ControllerTreeVisitor class
		 */
		if (controllerTree != null) {
			ControllerTreeVisitor treeVisitor = new ControllerTreeVisitor(controllerTree);
			controllerTree.apply(treeVisitor);
			viewDTO.setViewClass(treeVisitor.getCompilationUnit());
			viewDTO.setControllerClassName(treeVisitor.getRootClassName());
		}

		ViewTemplateTree viewTemplate = treeBuilder.getViewTemplateTree();
		/*
		 * tag tree is ready to write it out into the output
		 */
		if (viewTemplate != null) {
			WriterTagVisitor streamWriterVisitor = new WriterTagVisitor(os);
			viewTemplate.apply(streamWriterVisitor);
			viewDTO.setViewStream(streamWriterVisitor.getOutputStream());
		}

		viewDTO.setViewName(pageModel.getViewId());

		views.put(viewDTO.getViewId(), viewDTO);
	}

	protected AbstractTreeBuilder generateEntityPageViewAndController(EntityPageModel pageModel,
			IViewTemplateProvider tagTreeProvider, AbstractControllerNodeFactory controllerNodeProvider) {

		EntityPageTreeBuilder treeBuilder = new EntityPageTreeBuilder(pageModel, tagTreeProvider, controllerNodeProvider);

		for (AbstractEntityForm form : pageModel.getForms()) {
			if (form instanceof EntityForm) {
				EntityForm entityForm = (EntityForm) form;
				treeBuilder.addEntityForm(entityForm);

				for (EntityField field : entityForm.getFields()) {
					treeBuilder.addInputField(entityForm, field);
				}
			} else if (form instanceof EntityListForm) {
				EntityListForm entityListForm = (EntityListForm) form;
				treeBuilder.addEntityListForm(entityListForm);
				for (EntityField field : entityListForm.getFields()) {
					treeBuilder.addInputField(entityListForm, field);
				}
			}
		}

		return treeBuilder;
	}

	protected AbstractTreeBuilder generateListPageViewAndController(EntityListPageModel pageModel,
			IViewTemplateProvider tagTreeProvider, AbstractControllerNodeFactory controllerNodeProvider) {

		EntityListPageTreeBuilder treeBuilder = new EntityListPageTreeBuilder(pageModel, tagTreeProvider, controllerNodeProvider);

		for (ColumnModel column : pageModel.getColumns()) {
			treeBuilder.addColumn(column);
		}
		
		treeBuilder.buildQuery();
		
		IndexPageBuilder.getInstance().addView(pageModel.getViewId());
		
		return treeBuilder;
	}

	public ViewAndControllerDTO getViewAndControllerDTO(String viewId) {
		if (viewId == null || viewId.equals("")) {
			throw new IllegalArgumentException("View id parameter cannot be null!");
		}

		return views.get(viewId);
	}
}
