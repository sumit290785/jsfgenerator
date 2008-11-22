package jsfgenerator.entitymodel.forms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jsfgenerator.entitymodel.IEntityHandler;
import jsfgenerator.entitymodel.INamingContext;
import jsfgenerator.entitymodel.fields.EntityField;

/**
 * Abstract super class of a simple entity form or an entity list which contains
 * multiple entity forms!
 * 
 * A form has three tasks! A simple form is a container of an entity's fields,
 * and handles basic CRUD commands! This class supports Command design pattern
 * for handling commands on the entity!
 * 
 * The third task is to maintain other forms which are also EntityForms.
 * 
 * @author zoltan verebes
 * 
 */
public abstract class EntityForm implements IEntityHandler, INamingContext {
	
	private String entityName;
	
	private List<EntityField> fields = new ArrayList<EntityField>();
	
	public EntityForm(String entityName, List<EntityField> fields) {
		this.entityName = entityName;
		this.fields = fields;
	}

	// commands of the particular form
	private Set<Command> commands = new HashSet<Command>();


	public void setCommands(Set<Command> commands) {
		this.commands = commands;
	}

	/**
	 * Commands of the particular entity! They are displayed as actions on the
	 * page
	 * 
	 * @return
	 */
	public Set<Command> getCommands() {
		return commands;
	}

	public void addCommand(Command command) {
		commands.add(command);
	}
	
	/*
	 * (non-Javadoc)
	 * @see jsfgenerator.inspector.entitymodel.INamingContext#getName()
	 */
	public String getName() {
		return entityName;
	}
	
	public String getEntityName() {
		return entityName;
	}


	public List<EntityField> getFields() {
		return fields;
	}

}
