package jsfgenerator.entitymodel.utilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsfgenerator.ui.wizards.EntityWizardInput;

import org.eclipse.jdt.core.dom.Type;

/**
 * 
 * @author zoltan verebes
 * 
 */
public class EntityFactoryCommand {

	private Map<String, EntityWizardInput> entities = new HashMap<String, EntityWizardInput>();

	private String entityName;

	private Type type;

	private String fieldName;

	public void execute() {
		EntityWizardInput entity = entities.get(entityName);
		if (entity == null) {
			entity = new EntityWizardInput();
			entity.setName(entityName);
		}

		entity.addField(fieldName, type);
		entities.put(entityName, entity);
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public void setField(String fieldName, Type type) {
		this.fieldName = fieldName;
		this.type = type;
	}

	public List<EntityWizardInput> getEntities() {
		return Arrays.asList(entities.values().toArray(new EntityWizardInput[0]));
	}

}
