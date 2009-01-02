package jsfgenerator.generation.common.treebuilders;

import jsfgenerator.generation.controller.AbstractControllerNodeProvider;
import jsfgenerator.generation.controller.ControllerTree;
import jsfgenerator.generation.view.IViewTemplateProvider;
import jsfgenerator.generation.view.ViewTemplateTree;

public class EntityListPageTreeBuilder extends AbstractTreeBuilder {

	public EntityListPageTreeBuilder(IViewTemplateProvider tagTreeProvider, AbstractControllerNodeProvider controllerNodeProvider) {
		super(tagTreeProvider, controllerNodeProvider);
		// TODO list page
	}

	@Override
	public ControllerTree getControllerTree() {
		// TODO list page
		return null;
	}

	@Override
	public ViewTemplateTree getViewTemplateTree() {
		// TODO list page
		return null;
	}

}
