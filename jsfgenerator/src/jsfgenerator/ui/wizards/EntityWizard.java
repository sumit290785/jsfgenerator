package jsfgenerator.ui.wizards;

import java.util.List;

import jsfgenerator.inspector.entitymodel.IEntityModelBuilder;
import jsfgenerator.inspector.entitymodel.impl.JavaClassEntityModelBuilder;

import org.eclipse.jface.wizard.Wizard;

public class EntityWizard extends Wizard {

	private List<EntityWizardInput> entities;
	
	private EntitySelectionWizardPage entitySelectionWizardPage;

	public EntityWizard(List<EntityWizardInput> entities) {
		super();
		this.entities = entities;
		setWindowTitle("Entity wizard");
	}
	
	

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		entitySelectionWizardPage = new EntitySelectionWizardPage(entities);
		addPage(entitySelectionWizardPage);
		super.addPages();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		IEntityModelBuilder builder = new JavaClassEntityModelBuilder();
		
		for (EntityWizardInput entity : entitySelectionWizardPage.getSelectedEntities()) {
			builder.addEntity(entity);
		}
		
		// TODO: create a tag factory and call viewEngine to generate views!
		return true;
	}

}
