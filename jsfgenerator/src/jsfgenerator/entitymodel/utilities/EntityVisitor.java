package jsfgenerator.entitymodel.utilities;

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
	
	private EntityFactoryCommand command;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * TypeDeclaration)
	 */
	@Override
	public boolean visit(TypeDeclaration node) {

		if (isEntity(node)) {
			
			if (command == null) {
				throw new NullPointerException("Please, add a command to the visitor!");
			}
			
			for (FieldDeclaration field : node.getFields()) {
				for (Object obj : field.fragments()) {
					if (obj instanceof VariableDeclarationFragment) {

						VariableDeclarationFragment fragment = (VariableDeclarationFragment) obj;

						// check if the field has getter and setter and create EntityField
						if (containsGetter(node.getMethods(), fragment, field.getType())
								&& containsSetter(node.getMethods(), fragment, field.getType())) {
							command.setEntityName(node.getName().getFullyQualifiedName());
							command.setField(fragment.getName().getFullyQualifiedName(), field.getType());
							command.execute();
						}
					}
				}

			}
		}

		return true;
	}

	/**
	 * checks the type declaration weather it is an entity! A type declaration
	 * is entity if it is a class and it is annotated by @Entity
	 * 
	 * @param node
	 * @return true when node represents and entity class
	 */
	protected boolean isEntity(TypeDeclaration node) {
		return true;
	}

	protected boolean containsGetter(MethodDeclaration[] methods, VariableDeclarationFragment fragment, Type type) {
		for (MethodDeclaration method : methods) {
			if (EntityParser.isGetterOf(method, fragment, type)) {
				return true;
			}
		}
		return false;
	}

	protected boolean containsSetter(MethodDeclaration[] methods, VariableDeclarationFragment fragment, Type type) {
		for (MethodDeclaration method : methods) {
			if (EntityParser.isSetterOf(method, fragment, type)) {
				return true;
			}
		}
		return false;
	}
	
	
	public void setCommand(EntityFactoryCommand command) {
		this.command = command;
	}
}
