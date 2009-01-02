package jsfgenerator.generation.controller;

import java.util.List;

import jsfgenerator.entitymodel.pageelements.EntityForm;
import jsfgenerator.entitymodel.pageelements.EntityListForm;
import jsfgenerator.entitymodel.pages.EntityListPageModel;
import jsfgenerator.entitymodel.pages.EntityPageModel;
import jsfgenerator.generation.controller.nodes.ClassControllerNode;
import jsfgenerator.generation.controller.nodes.ControllerNode;
import jsfgenerator.generation.controller.nodes.FunctionControllerNode;

/**
 * @author zoltan verebes
 * 
 */
public abstract class AbstractControllerNodeFactory {

	public abstract ClassControllerNode createEntityPageClassNode(EntityPageModel model);
	
	public abstract ClassControllerNode createEntityListPageClassNode(EntityListPageModel model);

	public abstract List<ControllerNode> createEntityFormControllerNodes(EntityForm form);
	
	public abstract List<ControllerNode> createEntityListFormControllerNodes(EntityListForm form);
	
	public abstract FunctionControllerNode createListQueryFunctionNode(String query);

}
