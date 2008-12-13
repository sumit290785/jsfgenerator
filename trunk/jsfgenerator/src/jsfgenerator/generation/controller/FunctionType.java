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
	 * Empty block
	 */
	EMPTY,

	/**
	 * Init block
	 */
	INIT
}
