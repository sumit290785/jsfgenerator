package jsfgenerator.entitymodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsfgenerator.entitymodel.forms.AbstractEntityForm;
import jsfgenerator.entitymodel.forms.EntityField;
import jsfgenerator.entitymodel.forms.EntityForm;
import jsfgenerator.entitymodel.forms.EntityListForm;
import jsfgenerator.entitymodel.pages.AbstractPageModel;
import jsfgenerator.entitymodel.pages.EntityListPageModel;
import jsfgenerator.entitymodel.pages.EntityPageModel;
import jsfgenerator.generation.common.utilities.ClassNameUtils;
import jsfgenerator.generation.common.utilities.StringUtils;
import jsfgenerator.ui.model.EntityDescription;
import jsfgenerator.ui.model.EntityFieldDescription;

/**
 * MVC design pattern's model element can be defined in many different ways! A set of classes, an uml class diagram, an ecore model defined
 * by eclipse community, etc.
 * 
 * JSF generator uses the model (in MVC) to generate the view (MVC) xhtml files, and also to generate the controller (MVC) java classes! To
 * do this it uses an EntityModel object! EntityModel is an independent representation of the entity model! In other words. EntityModel is a
 * metamodel of the application model!
 * 
 * This is a builder class to build EntityModel meta-model
 * 
 * @author zoltan verebes
 * 
 */
public class EntityModelBuilder {

	/*
	 * entity model contains pages and their elements.
	 */
	protected EntityModel entityModel;

	protected Map<String, AbstractPageModel> entityPages;

	protected Map<String, AbstractPageModel> listPages;

	/**
	 * calls the clear method of the class to have an empty model
	 */
	public EntityModelBuilder() {
		clear();
	}

	/**
	 * 
	 * @return metamodel of the application model
	 */
	public EntityModel createEntityModel() {
		for (AbstractPageModel view : entityPages.values()) {
			entityModel.addPageModel(view);
		}

		return entityModel;
	}

	/**
	 * clears the model, entities, views
	 */
	public void clear() {
		entityModel = new EntityModel();
		entityPages = new HashMap<String, AbstractPageModel>();
	}

	/**
	 * adds an EntityPageModel to the entity model
	 * 
	 * @param entity
	 * @param viewId
	 */
	public void createEntityPageModel(String viewId, String entityClassName) {
		if (entityPages.containsKey(viewId)) {
			throw new IllegalArgumentException("View already in the model: " + viewId);
		}

		AbstractPageModel page = new EntityPageModel(viewId, entityClassName);
		entityPages.put(viewId, page);
	}

	/**
	 * adds an EntityListPageModel to the entity model
	 * 
	 * @param entity
	 * @param viewId
	 */
	public void createEntityListPageModel(String viewId, String entityClassName) {
		if (entityPages.containsKey(viewId)) {
			throw new IllegalArgumentException("View already in the model: " + viewId);
		}

		AbstractPageModel page = new EntityListPageModel(viewId, entityClassName);
		listPages.put(viewId, page);
	}

	public void addEntityForm(String viewId, EntityDescription entity, EntityFieldDescription field) {

		AbstractEntityForm form;
		if (field == null) {
			String formName = StringUtils.toDotSeparatedString(viewId, "entity");
			form = new EntityForm(formName, entity.getEntityClassName(), getEntityFields(entity), null);
		} else {
			String formName = StringUtils.toDotSeparatedString(viewId, field.getFieldName());
			form = new EntityForm(formName, entity.getEntityClassName(), getEntityFields(entity), field.getRelationshipToEntity());
		}

		getEntityPageModel(viewId).addForm(form);
	}

	public void addEntityListForm(String viewId, EntityDescription domainEntity, EntityFieldDescription listField,
			EntityDescription genericFieldDescription) {

		String simpleListFormName = ClassNameUtils.getSimpleClassName(listField.getFieldName());
		String formName = StringUtils.toDotSeparatedString(viewId, simpleListFormName);
		AbstractEntityForm form = new EntityListForm(formName, domainEntity.getEntityClassName(), genericFieldDescription
				.getEntityClassName(), getEntityFields(genericFieldDescription), listField.getRelationshipToEntity());
		getEntityPageModel(viewId).addForm(form);
	}

	protected EntityPageModel getEntityPageModel(String viewId) {
		AbstractPageModel view = getView(viewId);

		if (!(view instanceof EntityPageModel)) {
			throw new IllegalArgumentException("Simple entity form is applicable only for entity page models!");
		}

		return (EntityPageModel) view;
	}

	protected List<EntityField> getEntityFields(EntityDescription entity) {
		List<EntityField> fields = new ArrayList<EntityField>();
		for (EntityFieldDescription entityField : entity.getEntityFieldDescriptions()) {

			if (entityField.getInputTagName() != null && !entityField.getInputTagName().equals("")) {
				EntityField field = new EntityField(entityField.getFieldName(), entityField.getInputTagName());
				fields.add(field);
			}

		}

		return fields;
	}

	public void addFieldToList(String viewId, EntityDescription entity, EntityFieldDescription field) {
		// TODO: list
	}
	/**
	 * 
	 * @param viewId
	 * @return
	 */
	protected AbstractPageModel getView(String viewId) {
		return entityPages.get(viewId);
	}

	public boolean isViewSpecified(String viewId) {
		return entityPages.containsKey(viewId);
	}
}
