package jsfgenerator.ejb.sessionbeans;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;

/**
 * @author zoltan verebes
 * 
 */
@Stateless
public class PersistenceContextBean implements PersistenceContext {

	// jpa persistence context
	@javax.persistence.PersistenceContext
	private EntityManager em;

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.ejb.sessionbeans.PersistenceContext#delete(java.lang.Object)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(Object obj) {
		if (em.contains(obj)) {
			em.remove(obj);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.ejb.sessionbeans.PersistenceContext#save(java.lang.Object)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Object save(Object obj) {
		return em.merge(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.ejb.sessionbeans.PersistenceContext#load(long, java.lang.Class)
	 */
	public Object load(long id, Class<?> clazz) {
		Object obj = em.find(clazz, id);
		return obj;
	}

}
