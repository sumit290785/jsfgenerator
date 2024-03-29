package jsfgenerator.entitymodel.pages;

/**
 * Represents a page which is either a list page or a page managing a particular entity. T class is the domain entity which is managed by
 * the page.
 * 
 * @author zoltan verebes
 */
public abstract class AbstractPageModel {

	// view id is used to generate the name of the view file
	private String viewId;

	private String entityClassName;

	private String relatedPageViewId;

	public AbstractPageModel(String viewId, String entityClassName, String relatedViewId) {
		this.viewId = viewId;
		this.entityClassName = entityClassName;
		this.relatedPageViewId = relatedViewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getViewId() {
		return viewId;
	}

	public String getEntityClassName() {
		return entityClassName;
	}

	public void setRelatedPageViewId(String relatedPageViewId) {
		this.relatedPageViewId = relatedPageViewId;
	}

	public String getRelatedPageViewId() {
		return relatedPageViewId;
	}

}
