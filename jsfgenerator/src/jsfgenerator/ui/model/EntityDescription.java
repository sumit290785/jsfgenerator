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
	
	private boolean embeddable;

	private List<EntityFieldDescription> entityFieldDescriptions;
	
	public EntityDescription(TypeDeclaration node, boolean embeddable) {

		if (node == null) {
			throw new IllegalArgumentException("Type declaration node cannot be null!");
		}

		this.node = node;
		this.entityClassName = EntityClassParser.getFullyQualifiedName(node);
		this.embeddable = embeddable;
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

	public boolean isEmbeddable() {
		return embeddable;
	}

}
