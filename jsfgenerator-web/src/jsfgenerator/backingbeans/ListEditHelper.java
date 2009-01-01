package jsfgenerator.backingbeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsfgenerator.ejb.utilities.JndiLookupUtility;

public class ListEditHelper<T> {

	public static class EditHelperElementDelegate<T> {

		private int index;

		private ListEditHelper<T> editHelper;

		public EditHelperElementDelegate(int index, ListEditHelper<T> editHelper) {
			this.index = index;
			this.editHelper = editHelper;
		}

		public void save() {
			editHelper.save(index);
		}

		public void delete() {
			editHelper.delete(index);
		}

		public T getInstance() {
			return editHelper.getInstance(index);
		}
	}

	protected Class<T> entityClass;

	protected List<T> instances;

	protected Map<T, EditHelperElementDelegate<T>> elements;

	public ListEditHelper(List<T> instances, Class<T> entityClass) {

		if (instances == null) {
			instances = new ArrayList<T>();
		}

		if (entityClass == null) {
			throw new IllegalArgumentException(
					"Entity class parameter cannot be null! It is the class object of the generic parameter T! It is required to create new instance!");
		}

		this.entityClass = entityClass;
		this.instances = instances;

		this.elements = new HashMap<T, EditHelperElementDelegate<T>>();
		for (int i = 0; i < instances.size(); i++) {
			elements.put(instances.get(i), new EditHelperElementDelegate<T>(i, this));
		}
	}

	protected T getInstance(int index) {
		if (index < 0 && index >= instances.size()) {
			throw new IllegalArgumentException("Illegal index, no such element!");
		}
		return instances.get(index);
	}

	@SuppressWarnings("unchecked")
	protected void save(int index) {
		T instance = getInstance(index);
		if (instance != null) {
			elements.remove(instance);
			instance = (T) JndiLookupUtility.getInstance().getPersistenceContext().save(instance);
		}
		elements.put(instance, new EditHelperElementDelegate<T>(index, this));
		instances.set(index, instance);
	}

	protected void delete(int index) {
		T instance = getInstance(index);
		if (instance != null) {
			instances.remove(index);
			elements.remove(index);
			JndiLookupUtility.getInstance().getPersistenceContext().delete(instance);
		}
	}

	public void add() {
		T instance = null;
		try {
			instance = entityClass.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Could not instantiate entity class", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Could not instantiate entity class", e);
		}

		instances.add(instance);
		elements.put(instance, new EditHelperElementDelegate<T>(elements.size(), this));
	}

	public Map<T, EditHelperElementDelegate<T>> getElements() {
		return elements;
	}
	
	public List<T> getInstances() {
		return instances;
	}

}
