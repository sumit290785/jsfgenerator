package jsfgenerator.backingbeans;

import javax.persistence.EntityManager;

public abstract class AbstractListPage<T> {
	
	public abstract EntityManager getEntityManager();

}
