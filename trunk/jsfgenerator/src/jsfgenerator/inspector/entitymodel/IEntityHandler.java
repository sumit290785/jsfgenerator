package jsfgenerator.inspector.entitymodel;

import java.util.List;

import jsfgenerator.inspector.entitymodel.forms.EntityField;

/**
 * Interface to information about a single entity
 * 
 * @author zoltan verebes
 * 
 */
public interface IEntityHandler {

	/**
	 * @return name of the entity which is the same as its class's name
	 */
	public String getEntityName();

	/**
	 * @return fields of the entity which has getters and setters
	 */
	public List<EntityField> getFields();

}
