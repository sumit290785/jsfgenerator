package jsfgenerator.entitymodel.pages;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zoltan verebes
 * 
 * @param <T>
 */
public class EntityListPageModel extends AbstractPageModel {

	private List<EntityPageModel> entityPages = new ArrayList<EntityPageModel>();

	public void setEntityPages(List<EntityPageModel> entityPages) {
		this.entityPages = entityPages;
	}

	public List<EntityPageModel> getEntityPages() {
		return entityPages;
	}
	
	public void addEntityPage(EntityPageModel page) {
		entityPages.add(page);
	}

}
