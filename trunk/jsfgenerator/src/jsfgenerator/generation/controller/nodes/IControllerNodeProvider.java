package jsfgenerator.generation.controller.nodes;

import jsfgenerator.inspector.entitymodel.forms.SimpleEntityForm;

public interface IControllerNodeProvider {

	public ClassControllerNode createEntityPageClassNode(String viewId);

	public FieldControllerNode createSimpleFormControllerNode(SimpleEntityForm form);

	public FunctionControllerNode createSimpleFormGetterNode(SimpleEntityForm form);

}
