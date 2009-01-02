package jsfgenerator.ui.model;

import java.util.List;

public abstract class AbstractEntityDescriptionWrapper {
	
	protected EntityDescription entityDescription;
	
	protected String viewId = "";
	
	protected boolean embedded = false;
	
	// true if entity page for this entity is required. By default it is true
	protected boolean pageGenerated = true;
	
	protected List<AbstractEntityFieldDescriptionWrapper> fieldWrappers;
	
	public AbstractEntityDescriptionWrapper(EntityDescription entityDescription) {
		this.setEntityDescription(entityDescription);
	}

	public void setEntityDescription(EntityDescription entityDescription) {
		this.entityDescription = entityDescription;
	}

	public EntityDescription getEntityDescription() {
		return entityDescription;
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
	
	public AbstractEntityFieldDescriptionWrapper getFieldWrapper(EntityFieldDescription fieldDescription) {
		for (AbstractEntityFieldDescriptionWrapper wrapper : fieldWrappers) {
			if (wrapper.getEntityFieldDescription().equals(fieldDescription)) {
				return wrapper;
			}
		}
		
		return null;
	}
	
	public abstract List<AbstractEntityFieldDescriptionWrapper> getFieldWrappers();

	public void setPageGenerated(boolean pageGenerated) {
		this.pageGenerated = pageGenerated;
	}

	public boolean isPageGenerated() {
		return pageGenerated;
	}

}
