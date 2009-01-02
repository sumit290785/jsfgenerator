package jsfgenerator.generation.common.treebuilders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jsfgenerator.entitymodel.pageelements.AbstractEntityForm;
import jsfgenerator.entitymodel.pageelements.EntityField;
import jsfgenerator.entitymodel.pageelements.EntityForm;
import jsfgenerator.entitymodel.pageelements.EntityListForm;
import jsfgenerator.entitymodel.pages.EntityPageModel;
import jsfgenerator.generation.common.INameConstants;
import jsfgenerator.generation.common.utilities.StringUtils;
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

	private PlaceholderTagNode entityListFormPlaceholderNode;

	public EntityPageTreeBuilder(EntityPageModel model, IViewTemplateProvider tagTreeProvider,
			AbstractControllerNodeFactory controllerNodeProvider) {
		super(tagTreeProvider, controllerNodeProvider);
		this.model = model;
		init();
	}

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
	 * adds a simple entity form to the tag tree of the view and also to the controller tree for the backing bean class. field and getter
	 * method are required to be generated into the backing bean
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

		/*
		 * add the info to the controller tree for the backing bean
		 */
		classNode.addAllChildren(controllerNodeProvider.createEntityFormControllerNodes(form));
	}

	public void addEntityListForm(EntityListForm form) {
		ViewTemplateTree entityListFormTree = templateTreeProvider.getEntityListFormTemplateTree();

		String namespace = form.getEntityName() + INameConstants.EDITOR_FIELD_POSTFIX;
		ReferenceNameEvaluatorVisitor visitor = new ReferenceNameEvaluatorVisitor(namespace, form.getEntityName());
		visitor.setEntityFieldName("instances");

		entityListFormTree.apply(visitor);
		entityListFormTree.applyReferenceName(form.getEntityName());

		String varVariableName = getVarVariableName(entityListFormTree);
		ViewTemplateTree entityFormTree = templateTreeProvider.getEntityFormTemplateTree();
		visitor = new ReferenceNameEvaluatorVisitor(namespace, form.getEntityName());
		visitor.setVarVariable(varVariableName);
		entityFormTree.apply(visitor);

		AbstractTagNode entityFormPlaceholderNode = getFirstPlaceholderTagNodeByType(entityListFormTree,
				PlaceholderTagNodeType.ENTITY_FORM);

		if (entityFormPlaceholderNode == null) {
			throw new IllegalArgumentException(
					"Entity list form template tree does not contain placeholder tag for the entity form");
		}
		entityFormPlaceholderNode.addAllChildren(entityFormTree.getNodes());

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
	
}
