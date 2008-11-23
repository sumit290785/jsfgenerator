package jsfgenerator.generation.common.treebuilders;

import jsfgenerator.generation.controller.ControllerTree;
import jsfgenerator.generation.controller.AbstractControllerNodeProvider;
import jsfgenerator.generation.view.ITagTreeProvider;
import jsfgenerator.generation.view.TagTree;

/**
 * 
 * @author zoltan verebes
 *
 */
public abstract class AbstractTreeBuilder {
	
	protected ITagTreeProvider tagTreeProvider;
	
	protected AbstractControllerNodeProvider controllerNodeProvider;
	
	public AbstractTreeBuilder(ITagTreeProvider tagTreeProvider, AbstractControllerNodeProvider controllerNodeProvider) {
		this.tagTreeProvider = tagTreeProvider;
		this.controllerNodeProvider = controllerNodeProvider;
	}
	
	/**
	 * 
	 * @return
	 */
	public abstract TagTree getTagTree();
	
	/**
	 * 
	 * @return
	 */
	public abstract ControllerTree getControllerTree();

}
