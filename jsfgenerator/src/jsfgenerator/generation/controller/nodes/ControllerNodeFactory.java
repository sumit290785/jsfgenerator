package jsfgenerator.generation.controller.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import jsfgenerator.generation.controller.blockimplementation.InitStatementWrapper;
import jsfgenerator.generation.controller.blockimplementation.InitStatementWrapper.EditorType;

public class ControllerNodeFactory extends AbstractControllerNodeProvider {

	private static ControllerNodeFactory instance;

	private String packageName;

	private List<InitStatementWrapper> initStatementWrapper;

	public static ControllerNodeFactory getInstance() {
		if (instance == null) {
			instance = new ControllerNodeFactory();
		}

		return instance;
	}

	public ClassControllerNode createEntityPageClassNode(EntityPageModel model) {
		String className = NodeNameUtils.getEntityPageClassNameByUniqueName(model.getViewId());
		String superClassName = ClassNameUtils.addGenericParameter(INameConstants.ENTITY_PAGE_SUPER_CLASS, model
				.getEntityClassName());
		ClassControllerNode node = new ClassControllerNode(getPackageName(), className, superClassName);

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

		node.addChild(createInitFunctionNode());

		return node;
	}

	public List<ControllerNode> createSimpleFormControllerNodes(SimpleEntityForm form) {
		if (EntityRelationship.DOMAIN_ENTITY.equals(form.getRelationshipToEntity())) {
			// all of the required functionalities are in the super class
			return Collections.emptyList();
		}

		if (!EntityRelationship.ONE_TO_ONE.equals(form.getRelationshipToEntity())
				&& !EntityRelationship.MANY_TO_ONE.equals(form.getRelationshipToEntity())) {
			return Collections.emptyList();
		}
		List<ControllerNode> nodes = new ArrayList<ControllerNode>();
		/*
		 * add an edit helper and its getter
		 */
		String fieldType = ClassNameUtils.addGenericParameter(INameConstants.SIMPLE_FORM_FIELD_CLASS, form.getEntityClassName());
		String fieldName = NodeNameUtils.getControllerEditorFieldNameByCanonicalName(form.getEntityName());
		nodes.add(new FieldControllerNode(fieldName, fieldType, fieldType));
		nodes.add(createGetterFunctionControllerNode(fieldName, fieldType));

		// add it to the init function to get it initialized
		initStatementWrapper.add(new InitStatementWrapper(EditorType.EDIT_HELPER, fieldName, form.getEntityClassName(),
				ClassNameUtils.getSimpleClassName(form.getEntityClassName())));

		return nodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jsfgenerator.generation.controller.AbstractControllerNo{deProvider#createComplexFormControllerNodes(jsfgenerator.entitymodel.forms
	 * .ComplexEntityFormList, int)
	 */
	@Override
	public List<ControllerNode> createComplexFormControllerNodes(ComplexEntityFormList form) {

		if (!EntityRelationship.ONE_TO_MANY.equals(form.getRelationshipToEntity())
				&& !EntityRelationship.MANY_TO_MANY.equals(form.getRelationshipToEntity())) {
			return Collections.emptyList();
		}

		List<ControllerNode> nodes = new ArrayList<ControllerNode>();

		/*
		 * add an edit helper and its getter
		 */
		String fieldType = ClassNameUtils.addGenericParameter(INameConstants.COMPLEX_FORM_FIELD_CLASS, form.getSimpleForm()
				.getEntityClassName());
		String fieldName = NodeNameUtils.getControllerEditorFieldNameByCanonicalName(form.getEntityName());
		nodes.add(new FieldControllerNode(fieldName, fieldType, fieldType));
		nodes.add(createGetterFunctionControllerNode(fieldName, fieldType));

		// add it to the init function to get it initialized
		initStatementWrapper.add(new InitStatementWrapper(EditorType.LIST_EDIT_HELPER, form.getEntityName(), form.getSimpleForm()
				.getEntityClassName(), ClassNameUtils.getSimpleClassName(form.getSimpleForm().getEntityName())));

		return nodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejsfgenerator.generation.controller.AbstractControllerNodeProvider#createEntityListPageClassNode(jsfgenerator.entitymodel.pages.
	 * EntityListPageModel)
	 */
	@Override
	public ClassControllerNode createEntityListPageClassNode(EntityListPageModel model) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageName() {
		return packageName;
	}

	protected FunctionControllerNode createInitFunctionNode() {
		initStatementWrapper = new ArrayList<InitStatementWrapper>();
		return new FunctionControllerNode(INameConstants.ENTIT_PAGE_INIT_FUNCTION, FunctionType.INIT, initStatementWrapper);
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

}
