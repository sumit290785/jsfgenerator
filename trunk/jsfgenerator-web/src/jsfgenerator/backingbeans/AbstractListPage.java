package jsfgenerator.backingbeans;

import java.util.List;

import jsfgenerator.ejb.utilities.JndiLookupUtility;

/**
 * 
 * @author zoltan verebes
 *
 * @param <T>
 */
public abstract class AbstractListPage<T> {

	public List<?> getResultSet() {
		List<?> resultSet = (List<?>) JndiLookupUtility.getInstance().getPersistenceContext().execute(getQuery());
		return resultSet;
	}

	public abstract String getQuery();

}
