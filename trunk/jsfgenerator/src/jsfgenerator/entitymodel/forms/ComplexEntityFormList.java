package jsfgenerator.entitymodel.forms;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.entitymodel.fields.EntityField;

/**
 * Maintain a list of entity forms and supports commands for adding, removing
 * actions!
 * 
 * @author zoltan verebes
 * 
 * @param <T>
 */
public class ComplexEntityFormList extends EntityForm {
	
	// list of entity forms which are managed by this form list class
	private List<EntityForm> innerForms = new ArrayList<EntityForm>();
	
	public ComplexEntityFormList(String entityName, List<EntityField> fields) {
		super(entityName, fields);
		addCommand(Command.ADD);
		addCommand(Command.REMOVE);
	}

	public void setInnerForms(List<EntityForm> innerForms) {
		this.innerForms = innerForms;
	}

	public List<EntityForm> getInnerForms() {
		return innerForms;
	}

}
