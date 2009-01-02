package jsfgenerator.ui.model;

public class EntityFieldDescriptionListPageWrapper extends AbstractEntityFieldDescriptionWrapper {
	
	private boolean shown;
	
	private String fieldName;

	public EntityFieldDescriptionListPageWrapper(EntityFieldDescription entityFieldDescription) {
		super(entityFieldDescription);
	}
	
	@Override
	protected AbstractEntityDescriptionWrapper createEntityDescreptionWrapper(EntityDescription entityDescriptor) {
		return new EntityDescriptionListPageWrapper(entityDescriptor);
	}

	public void setShown(boolean shown) {
		this.shown = shown;
	}

	public boolean isShown() {
		return shown;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

}
