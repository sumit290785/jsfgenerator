package jsfgenerator.generation.controller.nodes;

import jsfgenerator.entitymodel.forms.SimpleEntityForm;
import jsfgenerator.generation.controller.IControllerNodeProvider;
import jsfgenerator.generation.controller.utilities.ControllerNodeUtils;

public class ControllerNodeFactory implements IControllerNodeProvider {

	// entity page controller super class
	// TODO: may be moved to external definition file with its generic
	// parameters
	private static final String SUPER_CLASS_NAME = null;

	private static final String SIMPLE_FORM_FIELD_CLASS = "jsfgenerator.xxx.EditHelper";

	private String packageName;

	public ControllerNodeFactory(String packageName) {
		this.packageName = packageName;
	}

	public ClassControllerNode createEntityPageClassNode(String viewId) {
		return new ClassControllerNode(packageName, viewId + "Page", SUPER_CLASS_NAME);
	}

	public FieldControllerNode createSimpleFormControllerNode(SimpleEntityForm form) {
		String fieldType = ControllerNodeUtils.addGenericParameter(SIMPLE_FORM_FIELD_CLASS, form.getFormName());
		return new FieldControllerNode(form.getFormName() + "Editor", fieldType, fieldType);
	}

	public FunctionControllerNode createSimpleFormGetterNode(SimpleEntityForm form) {
		return null;
	}

	public String getPackageName() {
		return packageName;
	}

}
