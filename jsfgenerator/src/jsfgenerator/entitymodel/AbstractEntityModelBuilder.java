package jsfgenerator.entitymodel;

import java.util.HashMap;
import java.util.Map;

import jsfgenerator.entitymodel.pages.AbstractPageModel;
import jsfgenerator.entitymodel.pages.EntityListPageModel;
import jsfgenerator.entitymodel.pages.EntityPageModel;

/**
 * MVC design pattern's model element can be defined in many different ways! A set of classes, an uml class diagram, an ecore model defined
 * by eclipse community, etc.
 * 
 * JSF generator uses the model (in MVC) to generate the view (MVC) xhtml files, and also to generate the controller (MVC) java classes! To
 * do this it uses an EntityModel object! EntityModel is a model (in MVC) independent representation model of the entity model! In other
 * words. EntityModel is a metamodel of the application model!
 * 
 * To handle different models an interface is required! This IEntityModelEngine interface is to build this bridge between the generator and
 * real application model!
 * 
 * T is the type of the entity which is used for building the model
 * 
 * @author zoltan verebes
 * 
 */
public abstract class AbstractEntityModelBuilder<TEntity, TField> {

	/*
	 * entity model contains pages and their elements.
	 */
	protected EntityModel entityModel;

	protected Map<String, AbstractPageModel> pages;

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
		for (AbstractPageModel view : pages.values()) {
			entityModel.addPageModel(view);
		}

		return entityModel;
	}

	/**
	 * clears the model, entities, views
	 */
	public void clear() {
		entityModel = new EntityModel();
		pages = new HashMap<String, AbstractPageModel>();
	}

	/**
	 * adds an EntityPageModel to the entity model
	 * 
	 * @param entity
	 * @param viewId
	 */
	public void createEntityPageModel(String viewId, String entityClassName) {
		if (pages.containsKey(viewId)) {
			throw new IllegalArgumentException("View already in the model: " + viewId);
		}

		AbstractPageModel page = new EntityPageModel(viewId, entityClassName);
		pages.put(viewId, page);
	}

	/**
	 * adds an EntityListPageModel to the entity model
	 * 
	 * @param viewId
	 */
	public void createEntityListPageModel(String viewId, String entityClassName) {
		if (pages.containsKey(viewId)) {
			throw new IllegalArgumentException("View already in the model: " + viewId);
		}

		AbstractPageModel page = new EntityListPageModel(viewId, entityClassName);
		pages.put(viewId, page);
	}

	/**
	 * 
	 * @param entity
	 * @param viewId
	 */
	public abstract void addSimpleEntityForm(String viewId, TEntity entity);

	/**
	 * 
	 * @param viewId
	 * @param domainEntity
	 * @param listField
	 * @param genericEntity
	 */
	public abstract void addComplexEntityFormList(TEntity domainEntity, TField listField);

	public void addEntityFormToList(EntityPageModel entityPage, String viewId) {

		if (entityPage == null) {
			throw new IllegalArgumentException("Entity page parameter cannot be null!");
		}

		AbstractPageModel page = pages.get(viewId);
		if (page == null) {
			throw new IllegalArgumentException("View is not in the model: " + viewId);
		}

		if (!(page instanceof EntityListPageModel)) {
			throw new IllegalArgumentException("View is not list page!");
		}

		((EntityListPageModel) page).addEntityPage(entityPage);
	}

	/**
	 * 
	 * @param viewId
	 * @return
	 */
	protected AbstractPageModel getView(String viewId) {
		return pages.get(viewId);
	}

	public boolean isViewSpecified(String viewId) {
		return pages.containsKey(viewId);
	}
}
