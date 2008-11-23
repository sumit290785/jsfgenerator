package jsfgenerator.generation.controller;

import java.util.List;

import jsfgenerator.entitymodel.forms.SimpleEntityForm;
import jsfgenerator.generation.controller.nodes.ClassControllerNode;
import jsfgenerator.generation.controller.nodes.ControllerNode;
import jsfgenerator.generation.controller.nodes.FunctionControllerNode;

/**
 * @author zoltan verebes
 * 
 */
public abstract class AbstractControllerNodeProvider {

	/**
	 * NONE bit mask means that neither getter nor setter function will be
	 * generated
	 */
	public static final int NONE = 0;

	/**
	 * SETTER bit mask causes setter function generated for the field
	 */
	public static final int SETTER = 1 << 1;

	/**
	 * SETTER bit mask causes getter function generated for the field
	 */
	public static final int GETTER = 1 << 2;

	public abstract ClassControllerNode createEntityPageClassNode(String viewId);

	public abstract List<ControllerNode> createSimpleFormControllerNodes(SimpleEntityForm form, int flag);

	public abstract FunctionControllerNode createFunctionControllerNode(String functionName, String returnType, String... args);
	/**
	 * checks if the style flag is 1 or 0
	 * 
	 * @param source
	 * @param target
	 * @return true when the particular flag is 1 else it is false
	 */
	protected boolean isFlagOn(int source, int target) {
		return (source & target) == target;
	}

}
