package jsfgenerator.generation.controller;

import java.util.List;

import jsfgenerator.entitymodel.forms.ComplexEntityFormList;
import jsfgenerator.entitymodel.forms.SimpleEntityForm;
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

	public abstract List<ControllerNode> createSimpleFormControllerNodes(SimpleEntityForm form);
	
	public abstract List<ControllerNode> createComplexFormControllerNodes(ComplexEntityFormList form);

}
