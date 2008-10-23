package jsfgenerator.inspector.entitymodel.pages;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.inspector.entitymodel.IEntityHandler;

/**
 * 
 * @author zoltan verebes
 * 
 * @param <T>
 */
public class EntityListPageModel extends PageModel implements IEntityHandler {

	private Class<?> entityClass;

	private List<EntityPageModel> entityPages = new ArrayList<EntityPageModel>();

	public EntityListPageModel(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public void setEntityPages(List<EntityPageModel> entityPages) {
		this.entityPages = entityPages;
	}

	public List<EntityPageModel> getEntityPages() {
		return entityPages;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}
}
