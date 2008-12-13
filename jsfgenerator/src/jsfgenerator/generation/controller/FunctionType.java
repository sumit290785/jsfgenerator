package jsfgenerator.generation.controller;

/**
 * Functions supported by BlockImplementationFactory. It generates code for the following function types.
 * 
 * @author zoltan verebes
 * 
 */
public enum FunctionType {

	/**
	 * standard POJO getter
	 */
	GETTER,

	/**
	 * standard POJO setter
	 */
	SETTER,

	/**
	 * Save function to persisting and merging entities into the database
	 */
	SAVE,

	/**
	 * Delete function to removing entities from the database
	 */
	DELETE,

	/**
	 * Empty block
	 */
	EMPTY,

	/**
	 * add an element to a list. if it happens in a managed environment the new entity is merged automatically into the list
	 */
	ADD,

	/**
	 * remove an element from a list. if it happens in a managed environment the new entity is merged automatically into the list
	 */
	REMOVE
}
