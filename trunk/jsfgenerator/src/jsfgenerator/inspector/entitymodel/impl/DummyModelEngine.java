package jsfgenerator.inspector.entitymodel.impl;

import jsfgenerator.dummy.MyEntity;
import jsfgenerator.dummy.MyEntity2;
import jsfgenerator.inspector.entitymodel.EntityModel;
import jsfgenerator.inspector.entitymodel.IEntityModelEngine;
import jsfgenerator.inspector.entitymodel.forms.SimpleEntityForm;
import jsfgenerator.inspector.entitymodel.pages.EntityPageModel;

public class DummyModelEngine implements IEntityModelEngine {

	public EntityModel getEntityModel() {
		EntityModel entityModel = new EntityModel();
		
		entityModel.addPageModel(createEntityModelPage());
		
		return entityModel;
	}

	private EntityPageModel createEntityModelPage() {
		EntityPageModel pageModel = new EntityPageModel();
		pageModel.addForm(new SimpleEntityForm(MyEntity.class));
		pageModel.addForm(new SimpleEntityForm(MyEntity2.class));
		pageModel.setViewId("MyEntity");
		return pageModel;
	}

}
