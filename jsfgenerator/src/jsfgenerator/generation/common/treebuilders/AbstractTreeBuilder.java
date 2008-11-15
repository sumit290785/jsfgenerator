package jsfgenerator.generation.common.treebuilders;

import jsfgenerator.generation.controller.ControllerTree;
import jsfgenerator.generation.controller.IControllerProvider;
import jsfgenerator.generation.view.ITagTreeProvider;
import jsfgenerator.generation.view.TagTree;

public abstract class AbstractTreeBuilder {
	
	protected IControllerProvider controllerProvider;
	
	protected ITagTreeProvider tagTreeProvider;
	
	public AbstractTreeBuilder(ITagTreeProvider tagTreeProvider, IControllerProvider controllerProvider) {
		this.controllerProvider = controllerProvider;
		this.tagTreeProvider = tagTreeProvider;
	}
	
	public abstract TagTree getTagTree();
	
	public abstract ControllerTree getControllerTree();

}
