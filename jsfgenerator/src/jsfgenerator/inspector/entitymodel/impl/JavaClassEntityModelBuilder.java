package jsfgenerator.inspector.entitymodel.impl;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.inspector.entitymodel.EntityModel;
import jsfgenerator.inspector.entitymodel.IEntityModelBuilder;
import jsfgenerator.ui.wizards.EntityWizardInput;

/**
 * 
 * @author zoltan verebes
 *
 */
public class JavaClassEntityModelBuilder implements IEntityModelBuilder {
	
	private List<EntityWizardInput> entitites = new ArrayList<EntityWizardInput>();

	public void clearModel() {
		// TODO Auto-generated method stub

	}

	public EntityModel createEntityModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addEntity(Object entity) {

		if (!(entity instanceof EntityWizardInput)) {
			throw new IllegalArgumentException("Model builder does not support the parameter as entity descriptor!");
		}
		
		// TODO: check if entity
		
		entitites.add((EntityWizardInput)entity);
	}
}
