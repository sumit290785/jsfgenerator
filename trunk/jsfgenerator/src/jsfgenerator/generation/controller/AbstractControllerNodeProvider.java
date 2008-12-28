package jsfgenerator.generation.controller;

import java.util.List;

import jsfgenerator.entitymodel.forms.EntityListForm;
import jsfgenerator.entitymodel.forms.EntityForm;
import jsfgenerator.entitymodel.pages.EntityListPageModel;
import jsfgenerator.entitymodel.pages.EntityPageModel;
import jsfgenerator.generation.controller.nodes.ClassControllerNode;
import jsfgenerator.generation.controller.nodes.ControllerNode;

/**
 * @author zoltan verebes
 * 
 */
public abstract class AbstractControllerNodeProvider {

	public abstract ClassControllerNode createEntityPageClassNode(EntityPageModel model);
	
	public abstract ClassControllerNode createEntityListPageClassNode(EntityListPageModel model);

	public abstract List<ControllerNode> createEntityFormControllerNodes(EntityForm form);
	
	public abstract List<ControllerNode> createEntityListFormControllerNodes(EntityListForm form);

}
