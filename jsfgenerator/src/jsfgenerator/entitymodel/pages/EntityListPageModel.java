package jsfgenerator.entitymodel.pages;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.entitymodel.pageelements.ColumnModel;

/**
 * An entity list has columns which are field of the particular entity or an entity in relationship with this domain entity of the page
 * 
 * @author zoltan verebes
 */
public class EntityListPageModel extends AbstractPageModel {

	private List<ColumnModel> columns = new ArrayList<ColumnModel>();

	public EntityListPageModel(String viewId, String entityClassName) {
		super(viewId, entityClassName);
	}

	public List<ColumnModel> getColumns() {
		return columns;
	}

	public void addColumn(ColumnModel column) {
		columns.add(column);
	}

}
