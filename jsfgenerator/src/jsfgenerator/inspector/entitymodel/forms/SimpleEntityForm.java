package jsfgenerator.inspector.entitymodel.forms;

/**
 * A simple entity form describes the simple and embedded fields of an entity
 * and supports commands for editing, saving, cancel editing of the entity.
 * 
 * @author zoltan verebes
 * 
 * @param <T>
 */
public class SimpleEntityForm extends EntityForm {
	
	public SimpleEntityForm(Class<?> entityClass) {
		super(entityClass);
		addCommand(Command.SAVE);
	}

}
