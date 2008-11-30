package jsfgenerator.entitymodel.forms;

import java.util.List;


/**
 * Maintain a list of entity forms and supports commands for adding, removing
 * actions!
 * 
 * @author zoltan verebes
 */
public class ComplexEntityFormList extends EntityForm {
	
	private SimpleEntityForm simpleForm;
	
	public ComplexEntityFormList(String entityName, List<EntityField> fields) {
		super(entityName, fields);
		addCommand(Command.ADD);
		addCommand(Command.REMOVE);
		
		simpleForm = new SimpleEntityForm(entityName, fields);
	}

	public SimpleEntityForm getSimpleForm() {
		return simpleForm;
	}

}
