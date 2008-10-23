package jsfgenerator.inspector.entitymodel.forms;

import java.util.HashSet;
import java.util.Set;

import jsfgenerator.inspector.entitymodel.IEntityHandler;

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
public abstract class EntityForm implements IEntityHandler {

	// commands of the particular form
	private Set<Command> commands = new HashSet<Command>();
	private Class<?> entityClass;

	public EntityForm(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

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

	/**
	 * The class managed by the entity form!
	 */
	public Class<?> getEntityClass() {
		return entityClass;
	}
}
