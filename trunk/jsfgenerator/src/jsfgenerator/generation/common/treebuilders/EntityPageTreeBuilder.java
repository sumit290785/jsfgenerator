package jsfgenerator.generation.common.treebuilders;

import java.util.Iterator;

import jsfgenerator.generation.common.utilities.Tags;
import jsfgenerator.generation.controller.ControllerTree;
import jsfgenerator.generation.controller.IControllerProvider;
import jsfgenerator.generation.view.ITagTreeProvider;
import jsfgenerator.generation.view.ProxyTag;
import jsfgenerator.generation.view.StaticTag;
import jsfgenerator.generation.view.TagNode;
import jsfgenerator.generation.view.TagTree;
import jsfgenerator.generation.view.ProxyTag.ProxyTagType;
import jsfgenerator.inspector.entitymodel.fields.EntityField;
import jsfgenerator.inspector.entitymodel.forms.SimpleEntityForm;

public class EntityPageTreeBuilder extends AbstractTreeBuilder {

	private TagTree tagTree;

	private ProxyTag formProxyTag;

	private String name;

	public EntityPageTreeBuilder(String name, ITagTreeProvider tagTreeProvider, IControllerProvider controllerProvider) {
		super(tagTreeProvider, controllerProvider);
		this.tagTree = tagTreeProvider.getEntityPageTagTree();
		this.name = name;
	}

	public void addSimpleForm(SimpleEntityForm form) {
		TagTree simpleFormTagTree = tagTreeProvider.getSimpleFormTagTree();
		simpleFormTagTree.applyReferenceName(form.getName());
		getFormProxyTag().addAllChildren(simpleFormTagTree.getNodes());
	}

	public void addInputField(SimpleEntityForm form, EntityField field) {
		TagNode inputProxyTag = Tags.getProxyTagByType(getFormTagByName(form.getName()), ProxyTagType.INPUT);

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
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return name;
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
