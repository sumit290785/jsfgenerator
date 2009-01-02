package jsfgenerator.ui.model;

public class EntityFieldDescriptionEntityPageWrapper extends AbstractEntityFieldDescriptionWrapper {

	public EntityFieldDescriptionEntityPageWrapper(EntityFieldDescription entityFieldDescription) {
		super(entityFieldDescription);
	}

	@Override
	protected AbstractEntityDescriptionWrapper createEntityDescreptionWrapper(EntityDescription entityDescriptor) {
		return new EntityDescriptionEntityPageWrapper(entityDescriptor);
	}

}
