package jsfgenerator.entitymodel;

import java.util.HashSet;
import java.util.Set;

import jsfgenerator.entitymodel.pages.AbstractPageModel;

/**
 * JSF generator project is to generate the view and the controller elements of
 * the MVC design pattern! The model of the view and controller is generated
 * based on the model! The model is constructed by Entities in a J2EE
 * environment!
 * 
 * View is a collection of JSF xhtml files in this environment and controllers
 * are java classes defined by EditHelper pattern! It is well described in the
 * book: J2EE core Patterns by Martin Fowler!
 * 
 * EntityModel is the domain element of transformed entity model! Entity model
 * can be anything: a set of entity beans, uml class diagram, ecore model, etc.
 * 
 * This domain element contains a set of page models!
 * 
 * TODO: it should contain the model menu and its menu items later
 * 
 * @author zoltan verebes
 * 
 */
public class EntityModel {

	private Set<AbstractPageModel> pageModels = new HashSet<AbstractPageModel>();

	public void setPageModels(Set<AbstractPageModel> pageModels) {
		this.pageModels = pageModels;
	}

	public Set<AbstractPageModel> getPageModels() {
		return pageModels;
	}
	
	public void addPageModel(AbstractPageModel pageModel) {
		pageModels.add(pageModel);
	}

}
