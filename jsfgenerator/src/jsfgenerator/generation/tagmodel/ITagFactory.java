package jsfgenerator.generation.tagmodel;

import jsfgenerator.generation.backingbean.naming.NamingContext;


/**
 * Implementation of this interface is responsible for xhtml tag model
 * generation! TagModel contains the information about the representation of the
 * required tags in the views! Tags are represented as trees! A node of a tree
 * is a tag, it knows its name, parameters and its children! A tag can have
 * multiple parameters, the parameters are name-value pairs. There are two types
 * of value of a parameter: static and expression! If it is expression a backing
 * bean (controller) must be assigned to it later.
 * 
 * Required tags and mappings for generation can be stored in xml files,
 * databases, repositories, hard coded java classes, etc!
 * 
 * Implementation of this interface factors a TagModel tree based on its
 * resource!
 * 
 * @author zoltan verebes
 * 
 */
public interface ITagFactory {

	/**
	 * 
	 * @return tag tree of an entity page
	 */
	public Tag getEntityPageTagTree();

	public Tag getListPageTagTree();

	public Tag getSimpleFormTagTree();

	public StaticTag getInputTag(Class<?> type, NamingContext namingContext);

}
