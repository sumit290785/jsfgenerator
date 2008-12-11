package jsfgenerator.generation.controller.nodes;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.entitymodel.forms.Command;
import jsfgenerator.entitymodel.forms.ComplexEntityFormList;
import jsfgenerator.entitymodel.forms.SimpleEntityForm;
import jsfgenerator.generation.common.INameConstants;
import jsfgenerator.generation.common.utilities.ClassNameUtils;
import jsfgenerator.generation.common.utilities.NodeNameUtils;
import jsfgenerator.generation.controller.AbstractControllerNodeProvider;
import jsfgenerator.generation.controller.nodes.FunctionControllerNode.FunctionType;

public class ControllerNodeFactory extends AbstractControllerNodeProvider {

	private String packageName;

	public ControllerNodeFactory(String packageName) {
		this.packageName = packageName;
	}

	public ClassControllerNode createEntityPageClassNode(String viewId) {
		ClassControllerNode node = new ClassControllerNode(packageName, NodeNameUtils.getEntityPageClassNameByUniqueName(viewId));

		/*node.addInterface("jsfgenerator.aaa.IProba");
		node.addInterface("jsfgenerator.bbb.IProba2");
		*/
		/*
		 * add empty functions for interface functions
		 */

		return node;
	}

	public List<ControllerNode> createSimpleFormControllerNodes(SimpleEntityForm form, int flag) {
		List<ControllerNode> nodes = new ArrayList<ControllerNode>();
		String fieldType = ClassNameUtils.addGenericParameter(INameConstants.SIMPLE_FORM_FIELD_CLASS, form.getEntityClassName());
		String fieldName = NodeNameUtils.getControllerEditorFieldNameByCanonicalName(form.getFormName());

		nodes.add(new FieldControllerNode(fieldName, fieldType, fieldType));

		if (isFlagOn(flag, GETTER)) {
			FunctionControllerNode getterNode = createGetterFunctionControllerNode(fieldName, fieldType);
			nodes.add(getterNode);
		}

		if (isFlagOn(flag, SETTER)) {
			FunctionControllerNode setterNode = createSetterFunctionControllerNode(fieldName, fieldType);
			nodes.add(setterNode);
		}
		
		for (Command command : form.getCommands()) {
			nodes.add(createCommandNodes(form, command));
		}
		
		return nodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jsfgenerator.generation.controller.AbstractControllerNodeProvider#createComplexFormControllerNodes(jsfgenerator.entitymodel.forms
	 * .ComplexEntityFormList, int)
	 */
	@Override
	public List<ControllerNode> createComplexFormControllerNodes(ComplexEntityFormList form) {

		List<ControllerNode> nodes = new ArrayList<ControllerNode>();

		for (Command command : form.getCommands()) {
			nodes.add(createCommandNodes(form, command));
		}
		
		return nodes;
	}

	public String getPackageName() {
		return packageName;
	}
	
	protected FunctionControllerNode createCommandNodes(ComplexEntityFormList form, Command command) {
		if (Command.ADD.equals(command)) {
			return createAddFunctionControllerNode(form.getFormName(), form.getSimpleForm().getEntityClassName());
		}
		
		if (Command.REMOVE.equals(command)) {
			return createRemoveFunctionControllerNode(form.getFormName(), form.getSimpleForm().getEntityClassName());
		}
		
		return null;
	}
	
	protected FunctionControllerNode createCommandNodes(SimpleEntityForm form, Command command) {
		
		if (Command.SAVE.equals(command)) {
			return createSaveFunctionControllerNode(form.getEntityClassName());
		}
		
		return null;
	}

	protected FunctionControllerNode createGetterFunctionControllerNode(String fieldName, String fieldType) {
		return new FunctionControllerNode(NodeNameUtils.getGetterName(fieldName), fieldType, FunctionType.GETTER, fieldName);
	}

	protected FunctionControllerNode createSetterFunctionControllerNode(String fieldName, String fieldType) {
		FunctionControllerNode node = new FunctionControllerNode(NodeNameUtils.getSetterName(fieldName), fieldType, FunctionType.SETTER, fieldName);
		node.addParameter(fieldName, fieldType);
		return node;
	}

	protected FunctionControllerNode createAddFunctionControllerNode(String listFieldName, String listElementType) {
		FunctionControllerNode node = new FunctionControllerNode(NodeNameUtils.getAddFunctionName(listElementType), FunctionType.ADD, listFieldName);
		node.addParameter("element", listElementType);
		return node;
	}
	
	protected FunctionControllerNode createRemoveFunctionControllerNode(String listFieldName, String listElementType) {
		FunctionControllerNode node = new FunctionControllerNode(NodeNameUtils.getRemoveFunctionName(listElementType), FunctionType.REMOVE, listFieldName);
		node.addParameter("element", listElementType);
		return node;
	}
	
	protected FunctionControllerNode createSaveFunctionControllerNode(String entityClassName) {
		FunctionControllerNode node = new FunctionControllerNode(NodeNameUtils.getSaveFunctionName(), FunctionType.SAVE, entityClassName);
		return node;
	}

}
