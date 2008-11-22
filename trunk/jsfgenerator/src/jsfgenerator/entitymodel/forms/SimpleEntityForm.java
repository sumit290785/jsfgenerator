package jsfgenerator.entitymodel.forms;

import java.util.List;

import jsfgenerator.entitymodel.fields.EntityField;

/**
 * A simple entity form describes the simple and embedded fields of an entity
 * and supports commands for editing, saving, cancel editing of the entity.
 * 
 * @author zoltan verebes
 * 
 * @param <T>
 */
public class SimpleEntityForm extends EntityForm {
	
	public SimpleEntityForm(String entityName, List<EntityField> fields) {
		super(entityName, fields);
		addCommand(Command.SAVE);
	}

}
