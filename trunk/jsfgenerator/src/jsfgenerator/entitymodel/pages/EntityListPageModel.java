package jsfgenerator.entitymodel.pages;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.entitymodel.IEntityHandler;
import jsfgenerator.entitymodel.fields.EntityField;

/**
 * 
 * @author zoltan verebes
 * 
 * @param <T>
 */
public class EntityListPageModel extends PageModel implements IEntityHandler {

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

	public String getEntityName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * fields to be displayed in the list! fields must be defined in the entity page models
	 */
	public List<EntityField> getFields() {
		// TODO Auto-generated method stub
		return null;
	}

}
