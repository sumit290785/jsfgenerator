package jsfgenerator.backingbeans;

import jsfgenerator.ejb.utilities.JndiLookupUtility;

/**
 * 
 * This class manages entity data with an entity manager, flushes the data to the database after saving, removing database actions. It also
 * provides setters and getters for JSF controller classes!
 * 
 * @author zoltan verebes
 * 
 * @param <T>
 */
public class EditHelper<T> {

	protected T instance;

	public EditHelper(T instance) {

		if (instance == null) {
			throw new IllegalArgumentException("Instance parameter cannot be null!");
		}

		this.instance = instance;
	}

	public T getInstance() {
		return instance;
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void save() {
		if (instance != null) {
			instance = (T) JndiLookupUtility.getInstance().getPersistenceContext().save(instance);
		}
	}

	public void delete() {
		if (instance != null) {
			JndiLookupUtility.getInstance().getPersistenceContext().delete(instance);
		}
	}
	
}
