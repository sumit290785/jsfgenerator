package jsfgenerator.inspector.entitymodel.pages;


/**
 * Represents a page which is either a list page or a page managing a particular entity.
 * T class is the domain entity which is managed by the page.
 * 
 * @author zoltan verebes
 */
public abstract class PageModel {
	
	// view id is used to generate the name of the view file
	private String viewId;

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getViewId() {
		return viewId;
	}

}
