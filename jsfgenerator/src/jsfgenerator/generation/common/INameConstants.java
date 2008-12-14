package jsfgenerator.generation.common;

public interface INameConstants {

	public static final String CONTROLLER_CLASS_NAME_TEMPLATE = "{0}Page";

	public static final String VIEW_NAME_EXTENSION = "xhtml";

	public static final String CLASS_FILENAME_EXTENSION = "java";

	public static final String SIMPLE_FORM_FIELD_CLASS = "jsfgenerator.backingbeans.EditHelper";

	public static final String COMPLEX_FORM_FIELD_CLASS = "jsfgenerator.backingbeans.ListEditHelper";

	public static final String ENTITY_PAGE_SUPER_CLASS = "jsfgenerator.backingbeans.AbstractEntityPage";

	public static final String ENTITY_PAGE_FIELD_ENTITY_MANAGER = "entityManager";

	public static final String ENTITY_PAGE_FIELD_ENTITY_CLASS = "entityClass";

	public static final String ENTITY_MANAGER_CLASS_NAME = "javax.persistence.EntityManager";

	public static final String CLASS_CLASS_NAME = "java.lang.Class";

	public static final String EDITOR_FIELD_POSTFIX = "EditHelper";

	public static final String DOMAIN_ENTITY_EDIT_HELPER = "entity" + EDITOR_FIELD_POSTFIX;

	public static final String ENTIT_PAGE_INIT_FUNCTION = "init";

	public static final String ENTITY_PAGE_POSTFIX = "Page";

	public static final String SETTER_PREFIX = "set";

	public static final String GETTER_PREFIX = "get";

	public static final String VIEW_XML_EXTENSION = "view";

	public static final String JAVA_DEFAULT_PACKAGE = "java.lang";

	public static final String STATELESS_ANNOTATION = "@javax.ejb.Stateless";

	public static final String PERSISTENCE_CONTEXT_ANNOTATION = "@javax.persistence.PersistenceContext";

	public static final String ENTITY_ANNOTATION = "@javax.persistence.Entity";

	public static final String EMBEDDED_ANNOTATION = "@javax.persistence.Embedded";

	public static final String ONE_TO_ONE_ANNOTATION = "@javax.persistence.OneToOne";
	
	public static final String ONE_TO_MANY_ANNOTATION = "@javax.persistence.OneToMany";

	public static final String MANY_TO_ONE_ANNOTATION = "@javax.persistence.ManyToOne";

	public static final String MANY_TO_MANY_ANNOTATION = "@javax.persistence.ManyToMany";

}
