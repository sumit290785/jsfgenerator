package jsfgenerator.ui.providers;

import jsfgenerator.ui.wizards.EntityWizardInput;
import jsfgenerator.ui.wizards.EntityWizardInput.EntityFieldInput;

import org.eclipse.jface.viewers.LabelProvider;

/**
 * Label provider on EntitySelectionWizardPage
 * 
 * @author zoltan verebes
 * 
 */
public class EntitySelectionLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof EntityWizardInput) {
			EntityWizardInput entity = (EntityWizardInput) element;
			return entity.getName();
		}

		if (element instanceof EntityFieldInput) {
			EntityFieldInput entry = (EntityFieldInput) element;
			StringBuffer buffer = new StringBuffer();
			buffer.append(entry.getFieldName());
			buffer.append(" (");
			buffer.append(entry.getFieldType().toString());
			buffer.append(")");
			return buffer.toString();
		}

		return super.getText(element);
	}

}
