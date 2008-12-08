package jsfgenerator.entitymodel.utilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsfgenerator.ui.wizards.EntityWizardInput;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * Visits all of the classes and stores the ones which are entities!
 * 
 * @author zoltan verebes
 * 
 */
public class EntityVisitor extends ASTVisitor {

	private Map<String, EntityWizardInput> entities = new HashMap<String, EntityWizardInput>();

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom. TypeDeclaration)
	 */
	@Override
	public boolean visit(TypeDeclaration node) {

		if (isEntity(node)) {

			for (FieldDeclaration field : node.getFields()) {
				for (Object obj : field.fragments()) {
					if (obj instanceof VariableDeclarationFragment) {
						VariableDeclarationFragment fragment = (VariableDeclarationFragment) obj;

						// check if the field has getter and setter and create EntityField
						if (hasGetter(node.getMethods(), fragment, field.getType()) && hasSetter(node.getMethods(), fragment, field.getType())) {
							createEntityWizardInput(node.getName().getFullyQualifiedName(), fragment.getName().getFullyQualifiedName(), field
									.getType());
						}
					}
				}

			}
		}

		return true;
	}

	/**
	 * checks the type declaration weather it is an entity! A type declaration is entity if it is a class and it is annotated by @Entity
	 * 
	 * @param node
	 * @return true when node represents and entity class
	 */
	protected boolean isEntity(TypeDeclaration node) {
		return true;
	}

	protected boolean hasGetter(MethodDeclaration[] methods, VariableDeclarationFragment fragment, Type type) {
		for (MethodDeclaration method : methods) {
			if (EntityParser.isGetterOf(method, fragment, type)) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasSetter(MethodDeclaration[] methods, VariableDeclarationFragment fragment, Type type) {
		for (MethodDeclaration method : methods) {
			if (EntityParser.isSetterOf(method, fragment, type)) {
				return true;
			}
		}
		return false;
	}

	protected void createEntityWizardInput(String entityName, String fieldName, Type type) {
		EntityWizardInput entity = entities.get(entityName);
		if (entity == null) {
			entity = new EntityWizardInput();
			entity.setName(entityName);
		}

		entity.addField(fieldName, type);
		entities.put(entityName, entity);
	}

	public List<EntityWizardInput> getEntityWizardInputs() {
		return Arrays.asList(entities.values().toArray(new EntityWizardInput[0]));
	}
}
