package jsfgenerator.generation.backingbean.naming;

import jsfgenerator.inspector.entitymodel.fields.EntityField;
import jsfgenerator.inspector.entitymodel.forms.EntityForm;

/**
 * An EntityForm contains a reference to an entity class and multiple references
 * to commands! To refer to the managed entity and its commands from jsf page
 * names of the methods on the backing bean are required!
 * 
 * @author zoltan verebes
 * 
 */
public class EntityFormNamingContext extends NamingContext {

	protected final static String ENTITY_ITEM = "item";

	protected final static String PARAMETER_VALUE = "value";

	protected String helperName;

	protected EntityForm entityForm;

	protected EntityField field;
	
	protected String viewBackingBean;

	public EntityFormNamingContext(String viewBackingBean, String helperName, EntityForm entityForm, EntityField field) {
		this.helperName = helperName;
		this.entityForm = entityForm;
		this.field = field;
		this.viewBackingBean = viewBackingBean;
	}

	/**
	 * Name of the helper bean
	 * 
	 * @return
	 */
	public String getHelperName() {
		return helperName;
	}

	/**
	 * Full reference from helper bean to the managed entity in the bean!
	 * 
	 * @return reference to entity name
	 */
	public String getEntityFieldName() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(viewBackingBean);
		buffer.append(SEPARATOR);
		buffer.append(helperName);
		buffer.append(SEPARATOR);
		buffer.append(ENTITY_ITEM);
		buffer.append(SEPARATOR);
		buffer.append(field.getName());
		return buffer.toString();
	}

	@Override
	protected String getReference(String parameterName) {
		if (parameterName == null || parameterName.equals("")) {
			throw new IllegalArgumentException("Prameter name parameter is null!");
		}

		if (PARAMETER_VALUE.equals(parameterName)) {
			return getEntityFieldName();
		}

		return null;
	}
	// TODO: command naming

}
