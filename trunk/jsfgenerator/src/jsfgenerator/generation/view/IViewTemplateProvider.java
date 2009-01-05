package jsfgenerator.generation.view;

import java.util.List;

/**
 * Implementation of this interface is responsible for xhtml tag model generation! TagModel contains the information about the
 * representation of the required tags in the views! Tags are represented as trees! A node of a tree is a tag, it knows its name, parameters
 * and its children! A tag can have multiple parameters, the parameters are name-value pairs. There are two types of value of a parameter:
 * static and expression!
 * 
 * Required tags and mappings for generation can be stored in xml files, databases, repositories, hard coded java classes, etc!
 * 
 * @author zoltan verebes
 * 
 */
public interface IViewTemplateProvider {

	public ViewTemplateTree getEntityPageTemplateTree();

	public ViewTemplateTree getEntityListPageTemplateTree();

	public ViewTemplateTree getEntityFormTemplateTree();

	public ViewTemplateTree getEntityListFormTemplateTree();

	public ViewTemplateTree getInputTemplateTree(String inputTagId);

	public List<String> getInputTagNames();
	
	public ViewTemplateTree getListColumnDataTemplateTree();
	
	public ViewTemplateTree getListColumnActionTemplateTree();
	
	public ViewTemplateTree getListColumnHeaderTemplateTree();
	
	public ViewTemplateTree getListCollectionColumnTemplateTree();
	
	public ViewTemplateTree getListCollectionColumnDataTemplateTree();
	
}
