package jsfgenerator.generation.common.utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * This singleton builder class is to help to build an EJB3 query for the list pages.
 * 
 * @author zoltan verebes
 * 
 */
public class QueryBuilder {

	protected static QueryBuilder instance;

	private String domainEntityClass;

	private List<String> singleReferences;

	private List<String> collectionReferences;

	// avoid multiple instantiations
	protected QueryBuilder() {
		clear();
	}

	public static QueryBuilder getInstance() {
		if (instance == null) {
			instance = new QueryBuilder();
		}
		return instance;
	}

	public void clear() {
		domainEntityClass = "";
		singleReferences = new ArrayList<String>();
		collectionReferences = new ArrayList<String>();
	}

	public String getQueryString() {
		if (domainEntityClass == null || domainEntityClass.equals("")) {
			return "";
		}

		String entityRef = domainEntityClass.toLowerCase().equals(domainEntityClass) ? "entity" : domainEntityClass.toLowerCase();
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("FROM ");
		buffer.append(domainEntityClass.toLowerCase());
		buffer.append(" ");
		buffer.append(entityRef);
		
		
		for (String ref : singleReferences) {
			buffer.append(", (");
			buffer.append(entityRef);
			buffer.append(".");
			buffer.append(ref);
			buffer.append(")");
		}
		
		for (String ref : collectionReferences) {
			buffer.append(" LEFT JOIN ");
			buffer.append(entityRef);
			buffer.append(".");
			buffer.append(ref);
		}
		
		return buffer.toString();
	}

	public void setDomainEntityClass(String domainEntityClass) {
		this.domainEntityClass = ClassNameUtils.getSimpleClassName(domainEntityClass);
	}

	public void addSingleReference(String singleReference) {
		this.singleReferences.add(singleReference);
	}

	public void addCollectionReference(String collectionReference) {
		this.collectionReferences.add(collectionReference);
	}

}
