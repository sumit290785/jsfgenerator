package jsfgenerator.inspector.entitymodel.forms;

/**
 * Type of entity field! Subclasses of this abstract class are associated with
 * output tags which are converted into JSF files! Association is known by the
 * implementation of ITagFactory!
 * 
 * Subclasses are singletons!
 * 
 * @author zoltan verebes
 * 
 */
public abstract class EntityFieldType {
	
	protected static EntityFieldType instance;

	protected EntityFieldType() {
	}
	
}
