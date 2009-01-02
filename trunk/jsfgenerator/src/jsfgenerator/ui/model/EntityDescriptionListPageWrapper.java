package jsfgenerator.ui.model;

import java.util.ArrayList;
import java.util.List;

public class EntityDescriptionListPageWrapper extends AbstractEntityDescriptionWrapper {

	public EntityDescriptionListPageWrapper(EntityDescription entityDescription) {
		super(entityDescription);
	}

	@Override
	public List<AbstractEntityFieldDescriptionWrapper> getFieldWrappers() {
		if (fieldWrappers == null) {
			fieldWrappers = new ArrayList<AbstractEntityFieldDescriptionWrapper>();
			for (EntityFieldDescription desc : entityDescription.getEntityFieldDescriptions()) {
				fieldWrappers.add(new EntityFieldDescriptionListPageWrapper(desc));
			}
		}

		return fieldWrappers;
	}

	public static List<EntityDescriptionListPageWrapper> createWrappers(List<EntityDescription> descriptions) {
		List<EntityDescriptionListPageWrapper> wrappers = new ArrayList<EntityDescriptionListPageWrapper>();

		for (EntityDescription desc : descriptions) {
			wrappers.add(new EntityDescriptionListPageWrapper(desc));
		}
		return wrappers;
	}

}
