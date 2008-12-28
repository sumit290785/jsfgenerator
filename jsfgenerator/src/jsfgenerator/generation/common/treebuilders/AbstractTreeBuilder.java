package jsfgenerator.generation.common.treebuilders;

import jsfgenerator.generation.controller.ControllerTree;
import jsfgenerator.generation.controller.AbstractControllerNodeProvider;
import jsfgenerator.generation.view.IViewTemplateProvider;
import jsfgenerator.generation.view.ViewTemplateTree;

/**
 * 
 * @author zoltan verebes
 *
 */
public abstract class AbstractTreeBuilder {
	
	protected IViewTemplateProvider templateTreeProvider;
	
	protected AbstractControllerNodeProvider controllerNodeProvider;
	
	public AbstractTreeBuilder(IViewTemplateProvider tagTreeProvider, AbstractControllerNodeProvider controllerNodeProvider) {
		this.templateTreeProvider = tagTreeProvider;
		this.controllerNodeProvider = controllerNodeProvider;
	}
	
	/**
	 * 
	 * @return
	 */
	public abstract ViewTemplateTree getViewTemplateTree();
	
	/**
	 * 
	 * @return
	 */
	public abstract ControllerTree getControllerTree();

}
