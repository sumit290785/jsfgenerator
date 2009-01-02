package jsfgenerator.ui.model;

import java.util.List;

import jsfgenerator.entitymodel.pageelements.EntityRelationship;
import jsfgenerator.generation.common.utilities.ClassNameUtils;

import org.eclipse.jdt.core.dom.TypeDeclaration;

public abstract class AbstractEntityFieldDescriptionWrapper {

	private EntityFieldDescription entityFieldDescription;

	private AbstractEntityDescriptionWrapper entityDescriptionWrapper;
	
	private AbstractEntityDescriptionWrapper entityWrapper;

	public AbstractEntityFieldDescriptionWrapper(EntityFieldDescription entityFieldDescription) {
		this.setEntityFieldDescription(entityFieldDescription);
	}

	public void setEntityFieldDescription(EntityFieldDescription entityFieldDescription) {
		this.entityFieldDescription = entityFieldDescription;
	}

	public EntityFieldDescription getEntityFieldDescription() {
		return entityFieldDescription;
	}

	public void setExternalForm(EntityRelationship relationship) {

		if (relationship == null) {
			this.entityDescriptionWrapper = null;
			return;
		}

		String className;
		if (EntityRelationship.EMBEDDED.equals(relationship) || EntityRelationship.ONE_TO_ONE.equals(relationship)
				|| EntityRelationship.MANY_TO_ONE.equals(relationship)) {
			className = ClassNameUtils.removeGenericParameters(entityFieldDescription.getClassName());
		} else {
			List<String> genericTypeList = ClassNameUtils.getGenericParameterList(entityFieldDescription.getClassName());
			className = ClassNameUtils.removeGenericParameters(genericTypeList.get(0));
		}

		TypeDeclaration typeNode = ProjectResourceProvider.getInstance().findSingleClassTypeDeclarationInEjbProject(className);
		this.entityDescriptionWrapper = createEntityDescreptionWrapper(new EntityDescription(typeNode));
		this.entityDescriptionWrapper.setEmbedded(true);
	}

	public void setEntityDescriptionWrapper(EntityDescriptionEntityPageWrapper entityDescriptionWrapper) {
		this.entityDescriptionWrapper = entityDescriptionWrapper;
	}

	public AbstractEntityDescriptionWrapper getEntityDescriptionWrapper() {
		return entityDescriptionWrapper;
	}

	public void setEntityWrapper(AbstractEntityDescriptionWrapper entityWrapper) {
		this.entityWrapper = entityWrapper;
	}

	public AbstractEntityDescriptionWrapper getEntityWrapper() {
		return entityWrapper;
	}
	
	protected abstract AbstractEntityDescriptionWrapper createEntityDescreptionWrapper(EntityDescription entityDescriptor);
}
