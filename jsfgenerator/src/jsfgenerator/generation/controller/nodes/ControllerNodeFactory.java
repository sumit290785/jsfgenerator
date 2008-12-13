package jsfgenerator.generation.controller.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jsfgenerator.entitymodel.forms.Command;
import jsfgenerator.entitymodel.forms.ComplexEntityFormList;
import jsfgenerator.entitymodel.forms.EntityRelationship;
import jsfgenerator.entitymodel.forms.SimpleEntityForm;
import jsfgenerator.entitymodel.pages.EntityListPageModel;
import jsfgenerator.entitymodel.pages.EntityPageModel;
import jsfgenerator.generation.common.INameConstants;
import jsfgenerator.generation.common.utilities.ClassNameUtils;
import jsfgenerator.generation.common.utilities.NodeNameUtils;
import jsfgenerator.generation.controller.AbstractControllerNodeProvider;
import jsfgenerator.generation.controller.FunctionType;

public class ControllerNodeFactory extends AbstractControllerNodeProvider {

	private String packageName;

	public ControllerNodeFactory(String packageName) {
		this.packageName = packageName;
	}

	public ClassControllerNode createEntityPageClassNode(EntityPageModel model) {
		String className = NodeNameUtils.getEntityPageClassNameByUniqueName(model.getViewId());
		String superClassName = ClassNameUtils.addGenericParameter(INameConstants.ENTITY_PAGE_SUPER_CLASS, model
				.getEntityClassName());
		ClassControllerNode node = new ClassControllerNode(packageName, className, superClassName);

		// annotation
		node.addAnnotation(INameConstants.STATELESS_ANNOTATION);

		// add implementation of abstract functions
		final String emFieldType = INameConstants.ENTITY_MANAGER_CLASS_NAME;
		final String emFieldName = INameConstants.ENTITY_PAGE_FIELD_ENTITY_MANAGER;
		final String ecFieldType = ClassNameUtils
				.addGenericParameter(INameConstants.CLASS_CLASS_NAME, model.getEntityClassName());
		final String ecFieldName = INameConstants.ENTITY_PAGE_FIELD_ENTITY_CLASS;

		FieldControllerNode emNode = new FieldControllerNode(emFieldName, emFieldType, emFieldType);
		emNode.addAnnotation(INameConstants.PERSISTENCE_CONTEXT_ANNOTATION);
		node.addChild(emNode);
		node.addChild(new FieldControllerNode(ecFieldName, ecFieldType, ecFieldType));
		node.addChild(createGetterFunctionControllerNode(emFieldName, emFieldType));
		node.addChild(createGetterFunctionControllerNode(ecFieldName, ecFieldType));

		return node;
	}

	public List<ControllerNode> createSimpleFormControllerNodes(SimpleEntityForm form) {
		if (EntityRelationship.DOMAIN_ENTITY.equals(form.getRelationshipToEntity())) {
			// all of the required functionalities are in the super class
			return Collections.emptyList();
		}

		if (EntityRelationship.ONE_TO_ONE.equals(form.getRelationshipToEntity())
				|| EntityRelationship.MANY_TO_ONE.equals(form.getRelationshipToEntity())) {
			List<ControllerNode> nodes = new ArrayList<ControllerNode>();
			/*
			 * add an edit helper and its getter
			 */
			String fieldType = ClassNameUtils.addGenericParameter(INameConstants.SIMPLE_FORM_FIELD_CLASS, form
					.getEntityClassName());
			String fieldName = NodeNameUtils.getControllerEditorFieldNameByCanonicalName(form.getEntityName());
			nodes.add(new FieldControllerNode(fieldName, fieldType, fieldType));
			nodes.add(createGetterFunctionControllerNode(fieldName, fieldType));

			return nodes;

		}

		return Collections.emptyList();
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

		return nodes;
	}

	public String getPackageName() {
		return packageName;
	}

	protected FunctionControllerNode createCommandNodes(ComplexEntityFormList form, Command command) {
		if (Command.ADD.equals(command)) {
			return createAddFunctionControllerNode(form.getEntityName(), form.getSimpleForm().getEntityClassName());
		}

		if (Command.REMOVE.equals(command)) {
			return createRemoveFunctionControllerNode(form.getEntityName(), form.getSimpleForm().getEntityClassName());
		}

		return null;
	}

	protected FunctionControllerNode createGetterFunctionControllerNode(String fieldName, String fieldType) {
		return new FunctionControllerNode(NodeNameUtils.getGetterName(fieldName), fieldType, FunctionType.GETTER, fieldName);
	}

	protected FunctionControllerNode createSetterFunctionControllerNode(String fieldName, String fieldType) {
		FunctionControllerNode node = new FunctionControllerNode(NodeNameUtils.getSetterName(fieldName), fieldType,
				FunctionType.SETTER, fieldName);
		node.addParameter(fieldName, fieldType);
		return node;
	}

	protected FunctionControllerNode createAddFunctionControllerNode(String listFieldName, String listElementType) {
		FunctionControllerNode node = new FunctionControllerNode(NodeNameUtils.getAddFunctionName(listElementType),
				FunctionType.ADD, listFieldName);
		node.addParameter("element", listElementType);
		return node;
	}

	protected FunctionControllerNode createRemoveFunctionControllerNode(String listFieldName, String listElementType) {
		FunctionControllerNode node = new FunctionControllerNode(NodeNameUtils.getRemoveFunctionName(listElementType),
				FunctionType.REMOVE, listFieldName);
		node.addParameter("element", listElementType);
		return node;
	}

	protected FunctionControllerNode createSaveFunctionControllerNode(String entityClassName) {
		FunctionControllerNode node = new FunctionControllerNode(NodeNameUtils.getSaveFunctionName(), FunctionType.SAVE,
				entityClassName);
		return node;
	}

	@Override
	public ClassControllerNode createEntityListPageClassNode(EntityListPageModel model) {
		// TODO Auto-generated method stub
		return null;
	}

}
