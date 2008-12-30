package jsfgenerator.ejb.sessionbeans;

import javax.ejb.Local;

/**
 * local interface to do transactional persistence stuff in the ejb project. It lives in the ejb container
 * 
 * @author zoltan verebes
 * 
 */
@Local
public interface PersistenceContext {

	/**
	 * to create or update an entity
	 * 
	 * @param obj
	 * @return
	 */
	public Object save(Object obj);

	/**
	 * to remove an entity from the persistence context (from the database)
	 * 
	 * @param obj
	 */
	public void delete(Object obj);

	/**
	 * to load an entity by its id and entity class
	 * 
	 * @param id
	 * @param clazz
	 * @return
	 */
	public Object load(long id, Class<?> clazz);

}
