package jsfgenerator.ui.astvisitors;

import jsfgenerator.entitymodel.forms.EntityRelationship;
import jsfgenerator.generation.common.INameConstants;
import jsfgenerator.generation.common.utilities.AnnotationNameUtils;
import jsfgenerator.generation.common.utilities.ClassNameUtils;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;

/**
 * AST Parser to parse a method declaration or variable fragment if that is annotated by entity relationship specific annotation! These
 * annotations are exclusive, so it stops parsing when one of the annotation is found.
 * 
 * @author zoltan verebes
 * 
 */
public class AnnotationASTVisitor extends ASTVisitor {

	// default value is DomainEntity
	private EntityRelationship relationshipToEntity = EntityRelationship.FIELD;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.NormalAnnotation)
	 */
	@Override
	public boolean visit(NormalAnnotation node) {
		checkAnnotation(node);
		return relationshipToEntity == EntityRelationship.FIELD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MarkerAnnotation)
	 */
	@Override
	public boolean visit(MarkerAnnotation node) {
		checkAnnotation(node);
		return relationshipToEntity == EntityRelationship.FIELD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SingleMemberAnnotation)
	 */
	@Override
	public boolean visit(SingleMemberAnnotation node) {
		checkAnnotation(node);
		return relationshipToEntity == EntityRelationship.FIELD;
	}

	/**
	 * 
	 * @return Relationship annotation of the method or variable or FIELD if none of the other annotations are found
	 */
	public EntityRelationship getEntityRelationship() {
		return relationshipToEntity;
	}

	private void checkAnnotation(Annotation node) {
		String className = ClassNameUtils.getSimpleClassName(node.getTypeName().getFullyQualifiedName());
		if (className.equals(AnnotationNameUtils.getSimpleAnnotationName(INameConstants.EMBEDDED_ANNOTATION))) {
			relationshipToEntity = EntityRelationship.EMBEDDED;
		}

		if (className.equals(AnnotationNameUtils.getSimpleAnnotationName(INameConstants.ONE_TO_ONE_ANNOTATION))) {
			relationshipToEntity = EntityRelationship.ONE_TO_ONE;
		}

		if (className.equals(AnnotationNameUtils.getSimpleAnnotationName(INameConstants.MANY_TO_ONE_ANNOTATION))) {
			relationshipToEntity = EntityRelationship.MANY_TO_ONE;
		}

		if (className.equals(AnnotationNameUtils.getSimpleAnnotationName(INameConstants.ONE_TO_MANY_ANNOTATION))) {
			relationshipToEntity = EntityRelationship.ONE_TO_MANY;
		}

		if (className.equals(AnnotationNameUtils.getSimpleAnnotationName(INameConstants.MANY_TO_MANY_ANNOTATION))) {
			relationshipToEntity = EntityRelationship.MANY_TO_MANY;
		}
	}

}
