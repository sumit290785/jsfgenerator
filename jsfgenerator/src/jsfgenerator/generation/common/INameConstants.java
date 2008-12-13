package jsfgenerator.generation.common;

public interface INameConstants {

	public static final String CONTROLLER_CLASS_NAME_TEMPLATE = "{0}Page";

	public static final String VIEW_NAME_EXTENSION = "xhtml";
	
	public static final String CLASS_FILENAME_EXTENSION = "java";

	public static final String SIMPLE_FORM_FIELD_CLASS = "jsfgenerator.backingbeans.EditHelper";
	
	public static final String ENTITY_PAGE_SUPER_CLASS = "jsfgenerator.backingbeans.AbstractEntityPage";
	
	public static final String ENTITY_PAGE_FIELD_ENTITY_MANAGER = "entityManager";
	
	public static final String ENTITY_PAGE_FIELD_ENTITY_CLASS = "entityClass";
	
	public static final String ENTITY_MANAGER_CLASS_NAME = "javax.persistence.EntityManager";
	
	public static final String CLASS_CLASS_NAME = "java.lang.Class";
	
	public static final String EDITOR_FIELD_POSTFIX = "EditHelper";

	public static final String ENTITY_PAGE_POSTFIX = "Page";

	public static final String SAVE_FUNCTION_NAME = "save";

	public static final String SETTER_PREFIX = "set";

	public static final String GETTER_PREFIX = "get";
	
	public static final String VIEW_XML_EXTENSION = "view";
	
	public static final String JAVA_DEFAULT_PACKAGE = "java.lang";
	
	public static final String STATELESS_ANNOTATION = "@javax.ejb.Stateless";
	
	public static final String PERSISTENCE_CONTEXT_ANNOTATION = "@javax.persistence.PersistenceContext";

}
