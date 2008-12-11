package jsfgenerator.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.ui.composites.EntityFieldSelectionComposite;
import jsfgenerator.ui.model.EntityDescription;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class EntityClassFieldSelectionWizardPage extends WizardPage {
	
	private EntityFieldSelectionComposite entityFieldSelectionComposite;

	protected EntityClassFieldSelectionWizardPage() {
		super("EntityClassFieldSelectionWizardPage");
		setTitle("Entity field selection");
		setDescription("Please, select the fields of the entities and their views");
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);
		setControl(composite);

		entityFieldSelectionComposite = new EntityFieldSelectionComposite(composite, SWT.NONE);
		entityFieldSelectionComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			List<EntityDescription> input = getSelectedEntityPageEntityDescription();
			entityFieldSelectionComposite.setInput(input);
			
			List<String> inputTagIds = ((MVCGenerationWizard) getWizard()).getInputTagIds();
			entityFieldSelectionComposite.setInputTagIds(inputTagIds);
		}
	}

	private List<EntityDescription> getSelectedEntityPageEntityDescription() {
		List<EntityDescription> entityDescriptions = new ArrayList<EntityDescription>();
		for (EntityDescription entityDescription : ((MVCGenerationWizard) getWizard()).getEntityDescriptions()) {
			if (entityDescription.isEntityPage()) {
				entityDescriptions.add(entityDescription);
			}
		}

		return entityDescriptions;
	}
	
	
}
