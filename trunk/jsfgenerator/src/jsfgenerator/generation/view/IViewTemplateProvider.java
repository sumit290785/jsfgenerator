package jsfgenerator.generation.view;

import java.util.List;

/**
 * Implementation of this interface is responsible for xhtml tag model generation! It is a compositional tree based model. TagModel contains
 * the information about the representation of the required tags in the views! Tags are represented as trees! A node of a tree is a tag, it
 * knows its name, parameters and its children! A tag can have multiple parameters, the parameters are name-value pairs. There are two types
 * of value of a parameter: static and expression!
 * 
 * An instance of a ViewTemplateTree is composition element of the view. It can keep another elements as children.
 * 
 * @author zoltan verebes
 * 
 */
public interface IViewTemplateProvider {

	/**
	 * @return entity page is the root of a tree for an entity page
	 */
	public ViewTemplateTree getEntityPageTemplateTree();

	/**
	 * @return entity page is the root of a tree for an entity list page
	 * 
	 */
	public ViewTemplateTree getEntityListPageTemplateTree();

	/**
	 * @return form is part of an entity page. it can be child of an entity page element
	 */
	public ViewTemplateTree getEntityFormTemplateTree();

	/**
	 * @return entity list form is an entity page element, it is under the root
	 */
	public ViewTemplateTree getEntityListFormTemplateTree();

	/**
	 * @return column data is part of an entity list page, it is under the root element
	 */
	public ViewTemplateTree getListColumnDataTemplateTree();

	/**
	 * @return list column action is a column for action and it has to have a place holder element for a single action
	 */
	public ViewTemplateTree getListColumnActionTemplateTree();

	/**
	 * @return list column header is an optional tree for the header of the list
	 */
	public ViewTemplateTree getListColumnHeaderTemplateTree();

	/**
	 * @return collection column is a like column data element, it is used when entity field is a collection
	 */
	public ViewTemplateTree getListCollectionColumnTemplateTree();

	/**
	 * @return collection column is a like column data element, it is used when entity field is a collection
	 */
	public ViewTemplateTree getListCollectionColumnDataTemplateTree();

	/**
	 * 
	 * @return action bar, it holds action for the particular tree. it can be a child of an entity page, entity list form, entity list page
	 */
	public ViewTemplateTree getActionBarTemplateTree();

	/**
	 * @param inputTagId
	 * @return input is a tree for an entity field
	 */
	public ViewTemplateTree getInputTemplateTree(String inputTagId);

	public List<String> getInputTagNames();
}
