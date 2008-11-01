package jsfgenerator.ui.wizards;

import java.io.OutputStream;
import java.util.List;

import jsfgenerator.generation.core.ViewEngine;
import jsfgenerator.generation.tagmodel.ITagTreeProvider;
import jsfgenerator.generation.tagmodel.impl.DummyTagFactory;
import jsfgenerator.inspector.entitymodel.AbstractEntityModelBuilder;
import jsfgenerator.inspector.entitymodel.EntityModel;
import jsfgenerator.inspector.entitymodel.impl.ASTEntityModelBuilder;

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
	 * 
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
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		AbstractEntityModelBuilder<EntityWizardInput> builder = new ASTEntityModelBuilder();

		for (EntityWizardInput entity : entitySelectionWizardPage.getSelectedEntities()) {
			builder.addEntity(entity);
			String viewId = entity.getName();
			if (!builder.isViewSpecified(viewId)) {
				builder.createEntityPageModel(viewId);
			}

			builder.addSimpleEntityForm(entity, viewId);
		}

		EntityModel entityModel = builder.createEntityModel();
		ITagTreeProvider tagFactory = new DummyTagFactory();

		ViewEngine engine = ViewEngine.getInstance();
		engine.generateViews(entityModel, tagFactory);
		
		for (OutputStream os : engine.getStreams()) {
			System.out.println(os);
		}

		return true;
	}

}
