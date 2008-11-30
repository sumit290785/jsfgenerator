package jsfgenerator.generation.common.treebuilders;

import java.util.Iterator;

import jsfgenerator.entitymodel.forms.ComplexEntityFormList;
import jsfgenerator.entitymodel.forms.EntityField;
import jsfgenerator.entitymodel.forms.EntityForm;
import jsfgenerator.entitymodel.forms.SimpleEntityForm;
import jsfgenerator.generation.common.visitors.ProxyTagVisitor;
import jsfgenerator.generation.controller.AbstractControllerNodeProvider;
import jsfgenerator.generation.controller.ControllerTree;
import jsfgenerator.generation.controller.nodes.ControllerNode;
import jsfgenerator.generation.view.ITagTreeProvider;
import jsfgenerator.generation.view.ProxyTag;
import jsfgenerator.generation.view.StaticTag;
import jsfgenerator.generation.view.TagNode;
import jsfgenerator.generation.view.TagTree;
import jsfgenerator.generation.view.ProxyTag.ProxyTagType;

/**
 * 
 * @author zoltan verebes
 * 
 */
public class EntityPageTreeBuilder extends AbstractTreeBuilder {

	private TagTree tagTree;

	// it is used to keep the information about the class, its fields and
	// functions which is created as backing bean
	private ControllerTree controllerTree;

	// default root element of the controller tree
	private ControllerNode classNode;

	// used for view name generation and controller class name generation.
	private String viewId;

	// the only form proxy tag in the tag tree
	private ProxyTag formProxyTag;

	public EntityPageTreeBuilder(String viewId, ITagTreeProvider tagTreeProvider, AbstractControllerNodeProvider controllerNodeProvider) {
		super(tagTreeProvider, controllerNodeProvider);
		this.viewId = viewId;
		init();
	}

	protected void init() {
		this.tagTree = tagTreeProvider.getEntityPageTagTree();
		this.controllerTree = new ControllerTree();

		/*
		 * add the root CLASS to the controller tree it will keep all of its elements as children in the tree
		 */
		classNode = controllerNodeProvider.createEntityPageClassNode(viewId);
		controllerTree.addNode(classNode);
	}

	/**
	 * adds a simple entity form to the tag tree of the view and also to the controller tree for the backing bean class. field and getter
	 * method are required to be generated into the backing bean
	 * 
	 * @param form
	 */
	public void addSimpleForm(SimpleEntityForm form) {
		/*
		 * add the info to the tag tree for the view
		 */
		TagTree simpleFormTagTree = tagTreeProvider.getSimpleFormTagTree();
		simpleFormTagTree.applyReferenceName(form.getFormName());
		getFormProxyTag().addAllChildren(simpleFormTagTree.getNodes());

		/*
		 * add the info to the controller tree for the backing bean
		 */
		classNode.addAllChildren(controllerNodeProvider.createSimpleFormControllerNodes(form, AbstractControllerNodeProvider.GETTER
				| AbstractControllerNodeProvider.SETTER));

	}

	public void addSimpleForm(ComplexEntityFormList form) {
		TagTree simpleFormTagTree = tagTreeProvider.getSimpleFormTagTree();
		TagNode formProxyTag = getProxyTagByType(getFormTagByName(form.getFormName()), ProxyTagType.FORM);
		formProxyTag.addAllChildren(simpleFormTagTree.getNodes());
	}

	public void addComplexFormTagTree(ComplexEntityFormList form) {
		TagTree complexFormList = tagTreeProvider.getComplexFormListTagTree();
		complexFormList.applyReferenceName(form.getFormName());
		getFormProxyTag().addAllChildren(complexFormList.getNodes());
		
		// TODO: add it to the controller tree too

	}

	public void addInputField(EntityForm form, EntityField field) {
		TagNode inputProxyTag = getProxyTagByType(getFormTagByName(form.getFormName()), ProxyTagType.INPUT);

		if (inputProxyTag == null) {
			throw new IllegalArgumentException("INPUT Proxy tag node is not found on the form passed to the function! Form name: "
					+ form.getFormName());
		}

		StaticTag inputTag = tagTreeProvider.getInputTag(field.getInputTagId());
		inputTag.setReferenceName(field.getFieldName());
		if (inputTag != null) {
			inputProxyTag.addChild(inputTag);
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
	public TagTree getTagTree() {
		return tagTree;
	}

	private ProxyTag getFormProxyTag() {
		if (formProxyTag == null) {
			formProxyTag = getProxyTagByType(tagTree, ProxyTagType.FORM);
		}

		return formProxyTag;
	}

	private TagNode getFormTagByName(String name) {
		Iterator<TagNode> it = getFormProxyTag().getChildren().iterator();
		while (it.hasNext()) {
			TagNode node = it.next();
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
	protected ProxyTag getProxyTagByType(TagTree tagTree, ProxyTagType type) {

		if (tagTree == null) {
			throw new IllegalArgumentException("Tag tree parameter cannot be null!");
		}

		if (type == null) {
			throw new IllegalArgumentException("Type parameter cannot be null!");
		}

		ProxyTagVisitor visitor = new ProxyTagVisitor(type);
		tagTree.apply(visitor);

		return visitor.getProxyTag();
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
	protected ProxyTag getProxyTagByType(TagNode node, ProxyTagType type) {

		if (node == null) {
			throw new IllegalArgumentException("Node parameter cannot be null!");
		}

		if (type == null) {
			throw new IllegalArgumentException("Type parameter cannot be null!");
		}

		TagTree tagTree = new TagTree();
		tagTree.addNode(node);
		return getProxyTagByType(tagTree, type);
	}
}