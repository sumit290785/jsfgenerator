package jsfgenerator.generation.common.treebuilders;

import java.util.Iterator;

import jsfgenerator.entitymodel.forms.ComplexEntityFormList;
import jsfgenerator.entitymodel.forms.EntityField;
import jsfgenerator.entitymodel.forms.EntityForm;
import jsfgenerator.entitymodel.forms.SimpleEntityForm;
import jsfgenerator.entitymodel.pages.EntityPageModel;
import jsfgenerator.generation.common.INameConstants;
import jsfgenerator.generation.common.utilities.StringUtils;
import jsfgenerator.generation.common.visitors.IndexVariableVisitor;
import jsfgenerator.generation.common.visitors.PlaceholderTagNodeVisitor;
import jsfgenerator.generation.common.visitors.ReferenceNameEvaluatorVisitor;
import jsfgenerator.generation.controller.AbstractControllerNodeProvider;
import jsfgenerator.generation.controller.ControllerTree;
import jsfgenerator.generation.controller.nodes.ControllerNode;
import jsfgenerator.generation.view.AbstractTagNode;
import jsfgenerator.generation.view.IViewTemplateProvider;
import jsfgenerator.generation.view.PlaceholderTagNode;
import jsfgenerator.generation.view.ViewTemplateTree;
import jsfgenerator.generation.view.PlaceholderTagNode.PlaceholderTagNodeType;

/**
 * TODO: do the same for list page - subclass the same class
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

	// the only form proxy tag in the tag tree
	private PlaceholderTagNode formPlaceholderTag;

	public EntityPageTreeBuilder(EntityPageModel model, IViewTemplateProvider tagTreeProvider,
			AbstractControllerNodeProvider controllerNodeProvider) {
		super(tagTreeProvider, controllerNodeProvider);
		this.model = model;
		init();
	}

	protected void init() {
		this.templateTree = templateTreeProvider.getEntityPageTemplateTree();
		this.controllerTree = new ControllerTree();

		/*
		 * add the root CLASS to the controller tree it will keep all of its elements as children in the tree
		 */
		classNode = controllerNodeProvider.createEntityPageClassNode(model);
		controllerTree.addNode(classNode);
	}

	/**
	 * adds a simple entity form to the tag tree of the view and also to the controller tree for the backing bean class. field and getter
	 * method are required to be generated into the backing bean
	 * 
	 * @param form
	 */
	public void addEntityForm(SimpleEntityForm form) {
		/*
		 * add the info to the tag tree for the view
		 */
		ViewTemplateTree simpleFormTagTree = templateTreeProvider.getEntityFormTemplateTree();
		simpleFormTagTree.applyReferenceName(form.getEntityName());

		String namespace = form.getEntityName() + INameConstants.EDITOR_FIELD_POSTFIX;
		ReferenceNameEvaluatorVisitor visitor = new ReferenceNameEvaluatorVisitor(namespace);
		simpleFormTagTree.apply(visitor);

		getEntityFormPlaceholder().addAllChildren(simpleFormTagTree.getNodes());

		/*
		 * add the info to the controller tree for the backing bean
		 */
		classNode.addAllChildren(controllerNodeProvider.createEntityFormControllerNodes(form));
	}

	public void addEntityForm(ComplexEntityFormList form, String indexVariableName) {
		AbstractTagNode formProxyTag = getPlaceholderTagNodeByType(getFormTagByName(form.getEntityName()),
				PlaceholderTagNodeType.ENTITY_FORM);

		if (formProxyTag == null) {
			throw new IllegalArgumentException("Complex form tag tree does not contain proxy tag: FORM");
		}

		ViewTemplateTree simpleFormTagTree = templateTreeProvider.getEntityFormTemplateTree();
		String namespace = form.getEntityName() + INameConstants.EDITOR_FIELD_POSTFIX;
		ReferenceNameEvaluatorVisitor visitor = new ReferenceNameEvaluatorVisitor(namespace);
		visitor.setParams(indexVariableName);
		simpleFormTagTree.apply(visitor);

		formProxyTag.addAllChildren(simpleFormTagTree.getNodes());
	}

	public void addEntityListFormTemplateTree(ComplexEntityFormList form) {
		ViewTemplateTree complexFormList = templateTreeProvider.getEntityListFormTemplateTree();

		String namespace = form.getEntityName() + INameConstants.EDITOR_FIELD_POSTFIX;
		ReferenceNameEvaluatorVisitor visitor = new ReferenceNameEvaluatorVisitor(namespace, "instances");
		String indexVariableName = getIndexVariableName(complexFormList);
		visitor.setParams(indexVariableName);
		complexFormList.apply(visitor);

		complexFormList.applyReferenceName(form.getEntityName());
		getEntityListFormPlaceholder().addAllChildren(complexFormList.getNodes());

		addEntityForm(form, indexVariableName);

		classNode.addAllChildren(controllerNodeProvider.createEntityListFormControllerNodes(form));
	}

	public void addInputField(EntityForm form, EntityField field) {
		AbstractTagNode formNode = getFormTagByName(form.getEntityName());
		AbstractTagNode inputProxyTag = getPlaceholderTagNodeByType(formNode, PlaceholderTagNodeType.INPUT);

		if (inputProxyTag == null) {
			throw new IllegalArgumentException(
					"INPUT Proxy tag node is not found on the form passed to the function! Form name: " + form.getEntityName());
		}

		ViewTemplateTree inputTemplateTree = templateTreeProvider.getInputTemplateTree(field.getInputTagId());

		String namespace;
		if (form instanceof SimpleEntityForm) {
			namespace = StringUtils.toDotSeparatedString(form.getEntityName() + INameConstants.EDITOR_FIELD_POSTFIX, "instance");
		} else {
			namespace = StringUtils.toDotSeparatedString(form.getEntityName() + INameConstants.EDITOR_FIELD_POSTFIX, "instance("
					+ getIndexVariableName(formNode) + ")");
		}

		ReferenceNameEvaluatorVisitor visitor = new ReferenceNameEvaluatorVisitor(namespace, field.getFieldName());
		for (AbstractTagNode tagNode : inputTemplateTree.getNodes()) {
			tagNode.apply(visitor);

			tagNode.setReferenceName(field.getFieldName());
			if (tagNode != null) {
				inputProxyTag.addChild(tagNode);
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

	protected PlaceholderTagNode getEntityFormPlaceholder() {
		if (formPlaceholderTag == null) {
			formPlaceholderTag = getPlaceholderTagNodeByType(templateTree, PlaceholderTagNodeType.ENTITY_FORM);
		}

		return formPlaceholderTag;
	}

	protected PlaceholderTagNode getEntityListFormPlaceholder() {
		if (formPlaceholderTag == null) {
			formPlaceholderTag = getPlaceholderTagNodeByType(templateTree, PlaceholderTagNodeType.ENTITY_LIST_FORM);
		}

		return formPlaceholderTag;
	}

	protected AbstractTagNode getFormTagByName(String name) {
		Iterator<AbstractTagNode> it = getEntityFormPlaceholder().getChildren().iterator();
		while (it.hasNext()) {
			AbstractTagNode node = it.next();
			if (node.getReferenceName().equals(name)) {
				return node;
			}
		}

		return null;
	}

	/**
	 * helper static function to find the first appearance of a proxy tag with the particular type in the tag tree
	 * 
	 * @param tagTree
	 *            source of the search
	 * @param type
	 *            target type of the proxy tag
	 * @return proxy tag with the particular type in the tag tree
	 */
	protected PlaceholderTagNode getPlaceholderTagNodeByType(ViewTemplateTree tagTree, PlaceholderTagNodeType type) {

		if (tagTree == null) {
			throw new IllegalArgumentException("Tag tree parameter cannot be null!");
		}

		if (type == null) {
			throw new IllegalArgumentException("Type parameter cannot be null!");
		}

		PlaceholderTagNodeVisitor visitor = new PlaceholderTagNodeVisitor(type);
		tagTree.apply(visitor);

		return visitor.getPlaceholderNode();
	}

	/**
	 * helper static function to find the first appearance of a proxy tag with the particular type in the subtree of the node
	 * 
	 * @param node
	 *            its subtree is the source of the search
	 * @param type
	 *            target type of the proxy tag
	 * @return proxy tag with the particular type in the subtree of the node
	 */
	protected PlaceholderTagNode getPlaceholderTagNodeByType(AbstractTagNode node, PlaceholderTagNodeType type) {

		if (node == null) {
			throw new IllegalArgumentException("Node parameter cannot be null!");
		}

		if (type == null) {
			throw new IllegalArgumentException("Type parameter cannot be null!");
		}

		ViewTemplateTree tagTree = new ViewTemplateTree();
		tagTree.addNode(node);
		return getPlaceholderTagNodeByType(tagTree, type);
	}

	protected String getIndexVariableName(AbstractTagNode node) {
		IndexVariableVisitor indexVariableVisitor = new IndexVariableVisitor();
		node.apply(indexVariableVisitor);

		if (!indexVariableVisitor.isIndexFound()) {
			return null;
		}

		return indexVariableVisitor.getIndexVariableName();
	}

	protected String getIndexVariableName(ViewTemplateTree tree) {
		for (AbstractTagNode node : tree.getNodes()) {
			String indexName = getIndexVariableName(node);
			if (indexName != null) {
				return indexName;
			}
		}

		return null;
	}

}
