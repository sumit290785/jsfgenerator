package jsfgenerator.backingbeans;

import java.util.List;

public class ListEditHelper<T> {

	protected Class<T> entityClass;

	protected List<T> instances;

	public ListEditHelper(List<T> instances, Class<T> entityClass) {

		if (instances == null) {
			throw new IllegalArgumentException("Instance list parameter cannot be null!");
		}

		if (entityClass == null) {
			throw new IllegalArgumentException(
					"Entity class parameter cannot be null! It is the class object of the generic parameter T! It is required to create new instance!");
		}

		this.entityClass = entityClass;
		this.instances = instances;
	}

	public T getInstance(int index) {
		if (index < 0 && index >= instances.size()) {
			throw new IllegalArgumentException("Illegal index, no such element!");
		}
		return instances.get(index);
	}

	public void save(int index) {
		T instance = getInstance(index);
		instances.set(index, instance);
	}

	public void delete(int index) {
		T instance = getInstance(index);
		if (instance != null) {
			instances.remove(index);
		}
	}

	public void newInstance() {
		T instance = null;
		try {
			instance = entityClass.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Could not instantiate entity class", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Could not instantiate entity class", e);
		}

		instances.add(instance);
	}

}
