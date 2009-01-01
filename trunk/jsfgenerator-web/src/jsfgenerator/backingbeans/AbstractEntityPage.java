package jsfgenerator.backingbeans;

import jsfgenerator.ejb.utilities.JndiLookupUtility;

/**
 * Generic abstract superclass for all of the entity page controller classes! It handles an instance of the entity T. Basic CRUD
 * functionality is implemented by edit helper JEE pattern classes.
 * 
 * @see jsfgenerator.backingbeans.EditHelper
 * 
 * @author zoltan verebes
 * 
 * @param <T>
 *            generic parameter is the entity class which is managed by the instance of this class
 */
public abstract class AbstractEntityPage<T> {

	protected Long entityId;

	protected EditHelper<T> entityEditHelper;

	public AbstractEntityPage() {
		setEntityId(null);
	}

	public void setEntityEditHelper(EditHelper<T> entityEditHelper) {
		this.entityEditHelper = entityEditHelper;
	}

	public EditHelper<T> getEntityEditHelper() {
		return entityEditHelper;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
		load();
	}

	public Long getEntityId() {
		return entityId;
	}

	@SuppressWarnings("unchecked")
	protected void load() {

		if (entityId == null) {
			createEntityInstance();
			return;
		}

		T entityInstance = (T) JndiLookupUtility.getInstance().getPersistenceContext().load(entityId, getEntityClass());

		if (entityInstance == null) {
			throw new NullPointerException("Entity not found! Entity class is " + getEntityClass() + " and entity id is "
					+ entityId);
		}

		entityEditHelper = new EditHelper<T>(entityInstance);
		init();
	}

	public void createEntityInstance() {
		entityId = null;
		T newEntityInstance;
		try {
			newEntityInstance = getEntityClass().newInstance();
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Could not instantiate new entity instance!", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Could not instantiate new entity instance!", e);
		}

		entityEditHelper = new EditHelper<T>(newEntityInstance);
		init();
	}

	public void init() {
	}
	
	public void save() {
		wire();
		entityEditHelper.save();
	}

	public abstract Class<T> getEntityClass();

	public abstract void wire();

}
