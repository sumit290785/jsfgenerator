package jsfgenerator.ui.model;

import java.util.List;

import jsfgenerator.ui.astvisitors.EntityClassParser;

import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * This class is the descriptor of the wizard input and also used to contained the selected data in the wizard. EntityParser 'findXXX'
 * functions are to instance the descriptions.
 * 
 * @author zoltan verebes
 * 
 */
public class EntityDescription {

	// class name of the entity
	private String entityClassName;

	// ast node of the entity in the AST tree built by Eclipse framework
	private TypeDeclaration node;

	// true if entity page for this entity is required. By default it is true
	private boolean entityPage = true;

	// true if list page for this entity is required. By default it is true
	private boolean listPage = true;

	private List<EntityFieldDescription> entityFieldDescriptions;

	private String viewId = "";

	private boolean embedded = false;

	public EntityDescription(TypeDeclaration node) {

		if (node == null) {
			throw new IllegalArgumentException("Type declaration node cannot be null!");
		}

		this.node = node;
		this.entityClassName = EntityClassParser.getFullyQualifiedName(node);
	}

	public boolean isEntityPage() {
		return entityPage;
	}

	public void setEntityPage(boolean entityPage) {
		this.entityPage = entityPage;
	}

	public boolean isListPage() {
		return listPage;
	}

	public void setListPage(boolean listPage) {
		this.listPage = listPage;
	}

	public String getEntityClassName() {
		return entityClassName;
	}

	public List<EntityFieldDescription> getEntityFieldDescriptions() {
		if (entityFieldDescriptions == null) {
			entityFieldDescriptions = EntityClassParser.findEntityFields(node);
		}

		return entityFieldDescriptions;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getViewId() {
		return viewId;
	}

	public void setEmbedded(boolean embedded) {
		this.embedded = embedded;
	}

	public boolean isEmbedded() {
		return embedded;
	}

}
