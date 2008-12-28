package jsfgenerator.entitymodel.pages;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.entitymodel.forms.AbstractEntityForm;

/**
 * It describes a page of a single entity type. Entity is a domain entity, which
 * means that it can refer to other entities! It knows the forms which appear on
 * the page of this page model!
 * 
 * @author zoltan verebes
 */
public class EntityPageModel extends AbstractPageModel {

	public EntityPageModel(String viewId, String entityClassName) {
		super(viewId, entityClassName);
	}

	// forms of the entity page. they are not necessarily EntityForm of T
	private List<AbstractEntityForm> forms = new ArrayList<AbstractEntityForm>();

	public void setForms(List<AbstractEntityForm> forms) {
		this.forms = forms;
	}

	public List<AbstractEntityForm> getForms() {
		return forms;
	}

	public void addForm(AbstractEntityForm form) {
		forms.add(form);
	}

	public void removeForm(AbstractEntityForm form) {
		forms.remove(form);
	}
}
