package jsfgenerator.ui.astvisitors;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.generation.common.utilities.ClassNameUtils;
import jsfgenerator.ui.model.EntityDescription;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * It walks the class nodes of the AST tree of the selected project, packages, files and collects all of the entities into
 * EntityDescriptions.
 * 
 * @see jsfgenerator.ui.model.EntityDescription
 * 
 * @author zoltan verebes
 * 
 */
public class EntityClassASTVisitor extends ASTVisitor {

	/**
	 * Subclass of ASTVisitor to parse annotations of the class
	 * 
	 * @author zoltan verebes
	 * 
	 */
	protected static class EntityAnnotationASTVisitor extends ASTVisitor {

		private final static String ENTITY = "Entity";

		private boolean isEntity = false;

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.NormalAnnotation)
		 */
		@Override
		public boolean visit(NormalAnnotation node) {
			isEntity |= isEntityAnnotation(node);
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MarkerAnnotation)
		 */
		@Override
		public boolean visit(MarkerAnnotation node) {
			isEntity |= isEntityAnnotation(node);
			return false;
		}

		public boolean isEntity() {
			return isEntity;
		}

		private boolean isEntityAnnotation(Annotation node) {
			String className = ClassNameUtils.getSimpleClassName(node.getTypeName().getFullyQualifiedName());
			return className.equals(ENTITY);
		}
	}

	private List<EntityDescription> entityDescriptions = new ArrayList<EntityDescription>();

	private String className;

	private TypeDeclaration singleTypeDeclaration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TypeDeclaration)
	 */
	@Override
	public boolean visit(TypeDeclaration node) {

		if (isEntity(node)) {

			if (className == null) {
				entityDescriptions.add(new EntityDescription(node));
				return true;
			} else if (className.equals( EntityClassParser.getFullyQualifiedName(node))) {
				singleTypeDeclaration = node;
				return false;
			}
		}

		return true;
	}

	public List<EntityDescription> getEntityDescriptions() {
		return entityDescriptions;
	}

	private boolean isEntity(TypeDeclaration node) {
		EntityAnnotationASTVisitor visitor = new EntityAnnotationASTVisitor();
		node.accept(visitor);
		return visitor.isEntity();
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	/**
	 * ASTNode of the class name configured with setClassName(). Class name is null implies that it will return null!
	 * 
	 * @return
	 */
	public TypeDeclaration getSingleClassTypeDeclaration() {
		return singleTypeDeclaration;
	}

}
