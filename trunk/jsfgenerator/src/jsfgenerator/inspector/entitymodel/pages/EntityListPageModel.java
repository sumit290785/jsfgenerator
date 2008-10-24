package jsfgenerator.inspector.entitymodel.pages;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.inspector.entitymodel.IEntityHandler;
import jsfgenerator.inspector.entitymodel.forms.EntityField;

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

	public String getEntityName() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<EntityField> getFields() {
		// TODO Auto-generated method stub
		return null;
	}

}
