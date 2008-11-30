package jsfgenerator.ui.providers;

import java.util.Collection;

import jsfgenerator.ui.wizards.EntityWizardInput;
import jsfgenerator.ui.wizards.EntityWizardInput.EntityFieldInput;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class EntitySelectionContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object parent) {
		if (parent instanceof EntityWizardInput) {
			EntityWizardInput entity = (EntityWizardInput) parent;
			return entity.getFields().toArray();
		}
		return null;
	}

	public Object getParent(Object child) {
		if (child instanceof EntityFieldInput) {
			return ((EntityFieldInput) child).getParent();
		}
			
		return null;
	}

	public boolean hasChildren(Object input) {
		if (input instanceof EntityWizardInput) {
			return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object input) {
		if (input instanceof Collection) {
			return ((Collection) input).toArray();
		}

		return null;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	}

}
