package jsfgenerator.inspector.entitymodel.pages;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.inspector.entitymodel.forms.EntityForm;

/**
 * It describes a page of a single entity type. Entity is a domain entity, which
 * means that it can refer to other entities! It knows the forms which appear on
 * the page of this page model!
 * 
 * @author zoltan verebes
 */
public class EntityPageModel extends PageModel {

	// forms of the entity page. they are not necessarily EntityForm of T
	private List<EntityForm> forms = new ArrayList<EntityForm>();

	public void setForms(List<EntityForm> forms) {
		this.forms = forms;
	}

	public List<EntityForm> getForms() {
		return forms;
	}

	public void addForm(EntityForm form) {
		forms.add(form);
	}

	public void removeForm(EntityForm form) {
		forms.remove(form);
	}
}
