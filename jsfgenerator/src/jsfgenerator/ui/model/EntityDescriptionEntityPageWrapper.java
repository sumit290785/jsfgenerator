package jsfgenerator.ui.model;

import java.util.ArrayList;
import java.util.List;

public class EntityDescriptionEntityPageWrapper extends AbstractEntityDescriptionWrapper {
	
	private String relatedPageViewId;

	public EntityDescriptionEntityPageWrapper(EntityDescription entityDescription, String relatedPageViewId) {
		super(entityDescription);
	}

	@Override
	public List<AbstractEntityFieldDescriptionWrapper> getFieldWrappers() {
		if (fieldWrappers == null) {
			fieldWrappers = new ArrayList<AbstractEntityFieldDescriptionWrapper>();
			for (EntityFieldDescription desc : entityDescription.getEntityFieldDescriptions()) {
				fieldWrappers.add(new EntityFieldDescriptionEntityPageWrapper(desc));
			}
		}

		return fieldWrappers;
	}

	public static List<EntityDescriptionEntityPageWrapper> createWrappers(List<EntityDescription> descriptions) {
		List<EntityDescriptionEntityPageWrapper> wrappers = new ArrayList<EntityDescriptionEntityPageWrapper>();

		for (EntityDescription desc : descriptions) {
			if (!desc.isEmbeddable())
			wrappers.add(new EntityDescriptionEntityPageWrapper(desc, ""));
		}
		return wrappers;
	}

	public void setRelatedPageViewId(String relatedPageViewId) {
		this.relatedPageViewId = relatedPageViewId;
	}

	public String getRelatedPageViewId() {
		return relatedPageViewId;
	}

}
