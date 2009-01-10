package jsfgenerator.generation.common.treebuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jsfgenerator.entitymodel.pageelements.AbstractEntityForm;
import jsfgenerator.entitymodel.pageelements.EntityField;
import jsfgenerator.entitymodel.pageelements.EntityForm;
import jsfgenerator.entitymodel.pageelements.EntityListForm;
import jsfgenerator.entitymodel.pages.EntityPageModel;
import jsfgenerator.generation.common.INameConstants;
import jsfgenerator.generation.common.utilities.ActionViewTemplateTreeBuilder;
import jsfgenerator.generation.common.utilities.StringUtils;
import jsfgenerator.generation.common.visitors.PlaceholderTagNodeVisitor;
import jsfgenerator.generation.common.visitors.ReferenceNameEvaluatorVisitor;
import jsfgenerator.generation.controller.AbstractControllerNodeFactory;
import jsfgenerator.generation.controller.ControllerTree;
import jsfgenerator.generation.controller.nodes.ControllerNode;
import jsfgenerator.generation.view.AbstractTagNode;
import jsfgenerator.generation.view.IViewTemplateProvider;
import jsfgenerator.generation.view.PlaceholderTagNode;
import jsfgenerator.generation.view.StaticTagNode;
import jsfgenerator.generation.view.ViewTemplateTree;
import jsfgenerator.generation.view.PlaceholderTagNode.PlaceholderTagNodeType;
import jsfgenerator.generation.view.parameters.TagAttribute;
import jsfgenerator.generation.view.parameters.XMLNamespaceAttribute;

/**
 * Builds the model of the controller and the view tree of an entity page. Models are represented in trees. The root of the tree is the
 * entityPage. It contains entity forms and entity list forms. It kind is up to the relationship of the entity to the base entity of the
 * page. Forms contain action bars for actions and place holder elements for the input fields. Input fields are attached to the place holder
 * elements of the tree
 * 
 * @author zoltan verebes
 * 
 */
public class EntityPageTreeBuilder extends AbstractTreeBuilder {

	private ViewTemplateTree templateTree;

	// it is used to keep the information about the class, its fields and
	// functions which is created as backing bean
	private ControllerTree controllerTree;

	// default root element of the controller tree
	private ControllerNode classNode;

	private EntityPageModel model;

	// the only entity form place holder tag in the tag tree
	private PlaceholderTagNode entityFormPlaceholderNode;

	// the entity list form place holder tag in the tag tree
	private PlaceholderTagNode entityListFormPlaceholderNode;

	public EntityPageTreeBuilder(EntityPageModel model, IViewTemplateProvider tagTreeProvider,
			AbstractControllerNodeFactory controllerNodeProvider) {
		super(tagTreeProvider, controllerNodeProvider);
		this.model = model;
		init();
	}

	/**
	 * initialize the root of the view template tree and the root of the controller tree
	 */
	protected void init() {
		this.templateTree = templateTreeProvider.getEntityPageTemplateTree();

		/*
		 * add jsfgen xml namespace
		 */
		TagAttribute attribute = new XMLNamespaceAttribute(INameConstants.JSFGEN_TAGLIB_XMLNS_PREFIX,
				INameConstants.JSFGEN_TAGLIB_XMLNS);
		((StaticTagNode) templateTree.getNodes().get(0)).addAttribute(attribute);

		this.controllerTree = new ControllerTree();

		/*
		 * add the root CLASS to the controller tree it will keep all of its elements as children in the tree
		 */
		classNode = controllerNodeProvider.createEntityPageClassNode(model);
		controllerTree.addNode(classNode);

		entityFormPlaceholderNode = getFirstPlaceholderTagNodeByType(templateTree, PlaceholderTagNodeType.ENTITY_FORM);
		entityListFormPlaceholderNode = getFirstPlaceholderTagNodeByType(templateTree, PlaceholderTagNodeType.ENTITY_LIST_FORM);

	}

	/**
	 * adds a simple entity form to the view template tree of the view and also to the controller tree for the backing bean class. field and
	 * getter method are required to be generated into the backing bean.
	 * 
	 * It requires a valid view template tree which is insured by the validation framework of Eclipse
	 * 
	 * @param form
	 */
	public void addEntityForm(EntityForm form) {
		/*
		 * add the info to the tag tree for the view
		 */
		ViewTemplateTree entityFormTree = templateTreeProvider.getEntityFormTemplateTree();
		entityFormTree.applyReferenceName(form.getEntityName());

		String namespace = form.getEntityName() + INameConstants.EDITOR_FIELD_POSTFIX;
		ReferenceNameEvaluatorVisitor visitor = new ReferenceNameEvaluatorVisitor(namespace, form.getEntityName());
		entityFormTree.apply(visitor);

		entityFormPlaceholderNode.addAllChildren(entityFormTree.getNodes());

		PlaceholderTagNode actionBarPlaceholderNode = getFirstPlaceholderTagNodeByType(templateTree,
				PlaceholderTagNodeType.ACTION_BAR);

		if (actionBarPlaceholderNode != null) {
			StaticTagNode saveNode = ActionViewTemplateTreeBuilder.getSaveActionNode();
			actionBarPlaceholderNode.addChild(saveNode);

			StaticTagNode deleteNode = ActionViewTemplateTreeBuilder.getRemoveActionNode();
			actionBarPlaceholderNode.addChild(deleteNode);

			visitor = new ReferenceNameEvaluatorVisitor(namespace, form.getEntityName());
			actionBarPlaceholderNode.apply(visitor);
		}

		/*
		 * add the info to the controller tree for the backing bean
		 */
		classNode.addAllChildren(controllerNodeProvider.createEntityFormControllerNodes(form));
	}

	/**
	 * adds a list entity form to the view template tree of the view and also to the controller tree for the backing bean class. It requires
	 * a valid view template tree which is insured by the validation framework of Eclipse
	 * 
	 * It must contain an action bar for the add command link, a form, an iterable part, an input place holder for the input fields for the
	 * entity and an action bar for save and remove command links
	 * 
	 * @param form
	 */
	public void addEntityListForm(EntityListForm form) {
		// root of the entity list form
		ViewTemplateTree entityListFormTree = templateTreeProvider.getEntityListFormTemplateTree();

		StaticTagNode variableNode = getVariableNode(entityListFormTree);

		if (variableNode == null) {
			throw new NullPointerException("Variable node not found! Please create a valid view descriptor");
		}

		// namespace is the entity instance out of the iterable part
		String namespace = form.getEntityName() + INameConstants.EDITOR_FIELD_POSTFIX;
		ReferenceNameEvaluatorVisitor visitor = new ReferenceNameEvaluatorVisitor(namespace, form.getEntityName());
		visitor.setEntityFieldName("instances");

		entityListFormTree.apply(visitor);
		entityListFormTree.applyReferenceName(form.getEntityName());

		PlaceholderTagNode actionBarPlaceholderNode = getFirstEntityListActionBar(entityListFormTree, variableNode);

		if (actionBarPlaceholderNode != null) {
			StaticTagNode addNode = ActionViewTemplateTreeBuilder.getAddActionNode();
			visitor = new ReferenceNameEvaluatorVisitor(namespace, form.getEntityName());
			addNode.apply(visitor);
			actionBarPlaceholderNode.addChild(addNode);
		}

		String varVariableName = getVarVariableName(entityListFormTree);

		visitor = new ReferenceNameEvaluatorVisitor(namespace, form.getEntityName());
		visitor.setVarVariable(varVariableName);

		for (AbstractTagNode child : variableNode.getChildren()) {
			child.apply(visitor);
		}

		actionBarPlaceholderNode = getFirstPlaceholderTagNodeByType(Arrays.asList((AbstractTagNode) variableNode),
				PlaceholderTagNodeType.ACTION_BAR);

		if (actionBarPlaceholderNode != null) {
			// remove element action
			StaticTagNode removeNode = ActionViewTemplateTreeBuilder.getRemoveActionNode();
			visitor = new ReferenceNameEvaluatorVisitor(namespace, form.getEntityName());
			removeNode.apply(visitor);
			actionBarPlaceholderNode.addChild(removeNode);
		}

		entityListFormPlaceholderNode.addAllChildren(entityListFormTree.getNodes());

		classNode.addAllChildren(controllerNodeProvider.createEntityListFormControllerNodes(form));
	}

	public void addInputField(EntityListForm form, EntityField field) {
		List<AbstractTagNode> nodes = getNodesByName(form.getEntityName(), entityListFormPlaceholderNode);
		addInputField(form, field, entityListFormPlaceholderNode, nodes, getVarVariableName(nodes));
	}

	public void addInputField(EntityForm form, EntityField field) {
		String namespace = StringUtils.toDotSeparatedString(form.getEntityName() + INameConstants.EDITOR_FIELD_POSTFIX,
				"instance");
		addInputField(form, field, entityFormPlaceholderNode, getNodesByName(form.getEntityName(), entityFormPlaceholderNode),
				namespace);
	}

	protected List<AbstractTagNode> getNodesByName(String name, PlaceholderTagNode placeholderNode) {
		List<AbstractTagNode> nodes = new ArrayList<AbstractTagNode>();
		Iterator<AbstractTagNode> it = placeholderNode.getChildren().iterator();
		while (it.hasNext()) {
			AbstractTagNode node = it.next();
			if (node.getReferenceName().equals(name)) {
				nodes.add(node);
			}
		}
		return nodes;
	}

	protected void addInputField(AbstractEntityForm form, EntityField field, PlaceholderTagNode placeholderNode,
			List<AbstractTagNode> nodes, String namespace) {
		AbstractTagNode inputPlaceholder = getFirstPlaceholderTagNodeByType(nodes, PlaceholderTagNodeType.INPUT);

		if (inputPlaceholder == null) {
			throw new IllegalArgumentException(
					"Input place holder node is not found on the form passed to the function! Form name: " + form.getEntityName());
		}

		ViewTemplateTree inputTemplateTree = templateTreeProvider.getInputTemplateTree(field.getInputTagName());

		String entityClassName = (form instanceof EntityForm) ? ((EntityForm) form).getEntityClassName()
				: ((EntityListForm) form).getEntityForm().getEntityClassName();

		ReferenceNameEvaluatorVisitor visitor = new ReferenceNameEvaluatorVisitor(namespace, entityClassName);
		visitor.setEntityFieldName(field.getFieldName());
		for (AbstractTagNode tagNode : inputTemplateTree.getNodes()) {
			tagNode.apply(visitor);

			tagNode.setReferenceName(field.getFieldName());
			if (tagNode != null) {
				inputPlaceholder.addChild(tagNode);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.core.AbstractTreeBuilder#getControllerTree()
	 */
	@Override
	public ControllerTree getControllerTree() {
		return controllerTree;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.core.AbstractTreeBuilder#getTagTree()
	 */
	@Override
	public ViewTemplateTree getViewTemplateTree() {
		return templateTree;
	}

	private PlaceholderTagNode getFirstEntityListActionBar(ViewTemplateTree tamplateTree, StaticTagNode variableNode) {

		if (tamplateTree == null) {
			throw new IllegalArgumentException("Tag tree parameter cannot be null!");
		}

		if (variableNode == null) {
			throw new IllegalArgumentException("variableNode parameter cannot be null!");
		}

		PlaceholderTagNodeVisitor visitor = new PlaceholderTagNodeVisitor(PlaceholderTagNodeType.ACTION_BAR);
		tamplateTree.apply(visitor, Arrays.asList((AbstractTagNode) variableNode));

		return visitor.getPlaceholderNode();
	}

}
