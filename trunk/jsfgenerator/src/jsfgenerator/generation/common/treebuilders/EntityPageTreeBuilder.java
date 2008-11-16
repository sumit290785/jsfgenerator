package jsfgenerator.generation.common.treebuilders;

import java.util.Iterator;

import jsfgenerator.generation.common.utilities.Tags;
import jsfgenerator.generation.controller.ControllerTree;
import jsfgenerator.generation.controller.nodes.ControllerNode;
import jsfgenerator.generation.controller.nodes.IControllerNodeProvider;
import jsfgenerator.generation.view.ITagTreeProvider;
import jsfgenerator.generation.view.ProxyTag;
import jsfgenerator.generation.view.StaticTag;
import jsfgenerator.generation.view.TagNode;
import jsfgenerator.generation.view.TagTree;
import jsfgenerator.generation.view.ProxyTag.ProxyTagType;
import jsfgenerator.inspector.entitymodel.fields.EntityField;
import jsfgenerator.inspector.entitymodel.forms.SimpleEntityForm;

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

	public EntityPageTreeBuilder(String viewId, ITagTreeProvider tagTreeProvider,
			IControllerNodeProvider controllerNodeProvider) {
		super(tagTreeProvider, controllerNodeProvider);
		this.viewId = viewId;
		init();
	}

	protected void init() {
		this.tagTree = tagTreeProvider.getEntityPageTagTree();
		this.controllerTree = new ControllerTree();

		/*
		 * add the root CLASS to the controller tree it will keep all of its
		 * elements as children in the tree
		 */
		classNode = controllerNodeProvider.createEntityPageClassNode(viewId);
		controllerTree.addNode(classNode);
	}

	/**
	 * adds a simple entity form to the tag tree of the view and also to the
	 * controller tree for the backing bean class. field and getter method are
	 * required to be generated into the backing bean
	 * 
	 * @param form
	 */
	public void addSimpleForm(SimpleEntityForm form) {
		/*
		 * add the info to the tag tree for the view
		 */
		TagTree simpleFormTagTree = tagTreeProvider.getSimpleFormTagTree();
		simpleFormTagTree.applyReferenceName(form.getName());
		getFormProxyTag().addAllChildren(simpleFormTagTree.getNodes());

		/*
		 * add the info to the controller tree for the backing bean
		 */
		classNode.addChild(controllerNodeProvider.createSimpleFormControllerNode(form));

		/*
		 * add getter function node for the field
		 */
	}

	public void addInputField(SimpleEntityForm form, EntityField field) {
		TagNode inputProxyTag = Tags.getProxyTagByType(getFormTagByName(form.getName()), ProxyTagType.INPUT);

		if (inputProxyTag == null) {
			throw new IllegalArgumentException(
					"INPUT Proxy tag node is not found on the form passed to the function! Form name: "
							+ form.getName());
		}

		StaticTag inputTag = tagTreeProvider.getInputTag(field.getType());
		inputTag.setReferenceName(field.getName());
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
			formProxyTag = Tags.getProxyTagByType(tagTree, ProxyTagType.FORM);
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
}
