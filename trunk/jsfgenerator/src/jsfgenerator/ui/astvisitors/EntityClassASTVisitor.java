package jsfgenerator.ui.astvisitors;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.generation.common.INameConstants;
import jsfgenerator.generation.common.utilities.AnnotationNameUtils;
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

		private boolean isEntity = false;
		
		private boolean isEmbeddable = false;

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.NormalAnnotation)
		 */
		@Override
		public boolean visit(NormalAnnotation node) {
			isEntity |= isEntityAnnotation(node);
			isEmbeddable |= isEmbeddableAnnotation(node);
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
			isEmbeddable |= isEmbeddableAnnotation(node);
			return false;
		}

		public boolean isEntity() {
			return isEntity;
		}
		
		public boolean isEmbeddable() {
			return isEmbeddable;
		}

		private boolean isEntityAnnotation(Annotation node) {
			String className = ClassNameUtils.getSimpleClassName(node.getTypeName().getFullyQualifiedName());
			return className.equals(AnnotationNameUtils.getSimpleAnnotationName(INameConstants.ENTITY_ANNOTATION));
		}
		
		private boolean isEmbeddableAnnotation(Annotation node) {
			String className = ClassNameUtils.getSimpleClassName(node.getTypeName().getFullyQualifiedName());
			return className.equals(AnnotationNameUtils.getSimpleAnnotationName(INameConstants.EMBEDDABLE_ANNOTATION));
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
		
		EntityAnnotationASTVisitor visitor = new EntityAnnotationASTVisitor();
		node.accept(visitor);

		if (visitor.isEntity() || visitor.isEmbeddable()) {

			if (className == null) {
				entityDescriptions.add(new EntityDescription(node, visitor.isEmbeddable()));
				return true;
			} else if (className.equals(EntityClassParser.getFullyQualifiedName(node))) {
				singleTypeDeclaration = node;
				return false;
			}
		}

		return true;
	}

	public List<EntityDescription> getEntityDescriptions() {
		return entityDescriptions;
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
