package jsfgenerator.generation.backingbean.naming;

import jsfgenerator.inspector.entitymodel.fields.EntityField;
import jsfgenerator.inspector.entitymodel.forms.EntityForm;
import jsfgenerator.inspector.entitymodel.pages.EntityPageModel;
import jsfgenerator.inspector.entitymodel.pages.PageModel;

/**
 * A singleton class which provides information for view engine about backing
 * bean naming for a page model and its content!
 * 
 * @author zoltan verebes
 * 
 */
public class BackingBeanNamingFactory {

	// Singleton instance
	protected static BackingBeanNamingFactory instance;

	protected static final String HOME = "Home";

	/*
	 * explanation of the name: a specialization of helper j2ee design pattern
	 * is the backing bean of a simple entity form
	 */
	protected static final String EDIT_HELPER = "EditHelper";

	public static BackingBeanNamingFactory getInstance() {
		if (instance == null) {
			instance = new BackingBeanNamingFactory();
		}
		return instance;
	}

	/**
	 * generates a unique name for the pageModel
	 * 
	 * @param pageModel
	 * @return
	 */
	public String getPageModelBackingBeanName(PageModel pageModel) {
		if (pageModel instanceof EntityPageModel) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(pageModel.getName().toLowerCase());
			buffer.append(HOME);
			return buffer.toString();
		}

		return null;
	}
	
	/**
	 * generates name for the entity form by its managed entity class's name!
	 * 
	 * @param form
	 * @return entity name wrapped into entity form naming wrapper
	 */
	public EntityFormNamingContext getEntityFormNamingContext(PageModel pageModel, EntityForm form, EntityField field) {

		if (form == null) {
			throw new IllegalArgumentException("Form parameter cannot be null!");
		}
		
		if (field == null) {
			throw new IllegalArgumentException("Field parameter cannot be null!");
		}

		String className = form.getEntityName();
		StringBuffer buffer = new StringBuffer();
		buffer.append(className.toLowerCase());
		buffer.append(EDIT_HELPER);
		
		return new EntityFormNamingContext(getPageModelBackingBeanName(pageModel), buffer.toString(), form, field);
	}

}
