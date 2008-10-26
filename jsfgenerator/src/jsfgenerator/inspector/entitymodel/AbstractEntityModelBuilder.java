package jsfgenerator.inspector.entitymodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsfgenerator.inspector.entitymodel.pages.EntityListPageModel;
import jsfgenerator.inspector.entitymodel.pages.EntityPageModel;
import jsfgenerator.inspector.entitymodel.pages.PageModel;

/**
 * MVC design pattern's model element can be defined in many different ways! A
 * set of classes, an uml class diagram, an ecore model defined by eclipse
 * community, etc.
 * 
 * JSF generator uses the model (in MVC) to generate the view (MVC) xhtml files,
 * and also to generate the controller (MVC) java classes! To do this it uses an
 * EntityModel object! EntityModel is a model (in MVC) independent
 * representation model of the entity model! In other words. EntityModel is a
 * metamodel of the application model!
 * 
 * To handle different models an interface is required! This IEntityModelEngine
 * interface is to build this bridge between the generator and real application
 * model!
 * 
 * T is the type of the entity which is used for building the model
 * 
 * @author zoltan verebes
 * 
 */
public abstract class AbstractEntityModelBuilder<T> {

	protected List<T> entities;

	protected EntityModel entityModel;
	
	protected Map<String, PageModel> pages;

	/**
	 * calls the clear method of the class to have an empty model
	 */
	public AbstractEntityModelBuilder() {
		clear();
	}

	/**
	 * 
	 * @return metamodel of the application model
	 */
	public EntityModel createEntityModel() {
		for (PageModel view : pages.values()) {
			entityModel.addPageModel(view);	
		}
		
		return entityModel;
	}

	/**
	 * clears the model, entities, views
	 */
	public void clear() {
		entityModel = new EntityModel();
		entities = new ArrayList<T>();
		pages = new HashMap<String, PageModel>();
	}

	/**
	 * adds an EntityPageModel to the entity model
	 * 
	 * @param entity
	 * @param viewId
	 */
	public void createEntityPageModel(String viewId) {
		if (pages.containsKey(viewId)) {
			throw new IllegalArgumentException("View already in the model: " + viewId);
		}
		
		PageModel page = new EntityPageModel();
		page.setViewId(viewId);
		pages.put(viewId, page);
	}

	/**
	 * adds an EntityListPageModel to the entity model
	 * 
	 * @param viewId
	 */
	public void createEntityListPageModel(String viewId) {
		if (pages.containsKey(viewId)) {
			throw new IllegalArgumentException("View already in the model: " + viewId);
		}
		
		PageModel page = new EntityListPageModel();
		page.setViewId(viewId);
		pages.put(viewId, page);
	}
	
	/**
	 * 
	 * @param entity
	 * @param viewId
	 */
	public abstract void addSimpleEntityForm(T entity, String viewId);
	
	/**
	 * 
	 * @param entity
	 * @param viewId
	 */
	public abstract void addComplexEntityFormList(T entity, String viewId);
	
	public void addEntityFormToList(EntityPageModel entityPage, String viewId) {
		
		if (entityPage == null) {
			throw new IllegalArgumentException("Entity page parameter cannot be null!");
		}
		
		PageModel page = pages.get(viewId);
		if (page == null) {
			throw new IllegalArgumentException("View is not in the model: " + viewId);
		}
		
		if (!(page instanceof EntityListPageModel)) {
			throw new IllegalArgumentException("View is not list page!");
		}
		
		((EntityListPageModel)page).addEntityPage(entityPage);
	}

	/**
	 * Adds an entity to the model and this input can be used for building the
	 * entity model
	 * 
	 * @param entity
	 */
	public void addEntity(T entity) {
		this.entities.add(entity);
	}

	/**
	 * Adds multiple entities to the model and these inputs can be used for
	 * building the entity model
	 * 
	 * @param entities
	 */
	public void addAllEntities(List<T> entities) {
		this.entities.addAll(entities);
	}
	
	/**
	 * 
	 * @param viewId
	 * @return
	 */
	protected PageModel getView(String viewId) {
		return pages.get(viewId);
	}
	
	public boolean isViewSpecified(String viewId) {
		return pages.containsKey(viewId);
	}
}
