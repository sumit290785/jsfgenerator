package jsfgenerator.generation.controller;

import jsfgenerator.entitymodel.forms.SimpleEntityForm;
import jsfgenerator.generation.controller.nodes.ClassControllerNode;
import jsfgenerator.generation.controller.nodes.FieldControllerNode;
import jsfgenerator.generation.controller.nodes.FunctionControllerNode;

public interface IControllerNodeProvider {

	public ClassControllerNode createEntityPageClassNode(String viewId);

	public FieldControllerNode createSimpleFormControllerNode(SimpleEntityForm form);

	public FunctionControllerNode createSimpleFormGetterNode(SimpleEntityForm form);

}
