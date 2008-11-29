package jsfgenerator.generation.controller.nodes;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.entitymodel.forms.SimpleEntityForm;
import jsfgenerator.generation.controller.AbstractControllerNodeProvider;
import jsfgenerator.generation.controller.nodes.FunctionControllerNode.FunctionType;
import jsfgenerator.generation.controller.utilities.ControllerNodeUtils;

public class ControllerNodeFactory extends AbstractControllerNodeProvider {

	// entity page controller super class
	// TODO: may be moved to external definition file with its generic
	// parameters
	private static final String SUPER_CLASS_NAME = "jsfgenerator.xxx.AbstractHome";

	private static final String SIMPLE_FORM_FIELD_CLASS = "jsfgenerator.xxx.EditHelper";

	private String packageName;

	public ControllerNodeFactory(String packageName) {
		this.packageName = packageName;
	}

	public ClassControllerNode createEntityPageClassNode(String viewId) {
		ClassControllerNode node = new ClassControllerNode(packageName, viewId + "Page", SUPER_CLASS_NAME);
		node.addInterface("jsfgenerator.aaa.IProba");
		node.addInterface("jsfgenerator.bbb.IProba2");

		return node;
	}

	public List<ControllerNode> createSimpleFormControllerNodes(SimpleEntityForm form, int flag) {
		List<ControllerNode> nodes = new ArrayList<ControllerNode>();
		String fieldType = ControllerNodeUtils.addGenericParameter(SIMPLE_FORM_FIELD_CLASS, form.getFormName());
		nodes.add(new FieldControllerNode(form.getFormName() + "Editor", fieldType, fieldType));

		if (isFlagOn(flag, GETTER)) {
			FunctionControllerNode getterNode = createGetterFunctionControllerNode(form.getFormName() + "Editor", fieldType);
			nodes.add(getterNode);
		}

		if (isFlagOn(flag, SETTER)) {
			FunctionControllerNode setterNode = createSetterFunctionControllerNode(form.getFormName() + "Editor", fieldType);
			nodes.add(setterNode);
		}

		return nodes;
	}

	public String getPackageName() {
		return packageName;
	}

	protected FunctionControllerNode createGetterFunctionControllerNode(String fieldName, String fieldType) {
		return new FunctionControllerNode(getGetterName(fieldName), fieldType, FunctionType.GETTER, fieldName);
	}

	protected FunctionControllerNode createSetterFunctionControllerNode(String fieldName, String fieldType) {
		FunctionControllerNode node = new FunctionControllerNode(getSetterName(fieldName), fieldType, FunctionType.SETTER, fieldName);
		node.addParameter(fieldName, fieldType);
		return node;
	}

	private String getSetterName(String fieldName) {
		if (fieldName == null || fieldName.equals("")) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("set");
		buffer.append(fieldName.substring(0, 1).toUpperCase());

		if (fieldName.length() > 1) {
			buffer.append(fieldName.substring(1));
		}

		return buffer.toString();
	}

	private String getGetterName(String fieldName) {
		if (fieldName == null || fieldName.equals("")) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("get");
		buffer.append(fieldName.substring(0, 1).toUpperCase());

		if (fieldName.length() > 1) {
			buffer.append(fieldName.substring(1));
		}

		return buffer.toString();
	}

}