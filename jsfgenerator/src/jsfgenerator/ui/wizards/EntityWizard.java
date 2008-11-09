package jsfgenerator.ui.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import jsfgenerator.generation.core.ViewEngine;
import jsfgenerator.generation.tagmodel.ITagTreeProvider;
import jsfgenerator.generation.tagmodel.impl.TagTreeParser;
import jsfgenerator.inspector.entitymodel.AbstractEntityModelBuilder;
import jsfgenerator.inspector.entitymodel.EntityModel;
import jsfgenerator.inspector.entitymodel.impl.ASTEntityModelBuilder;

import org.eclipse.jface.wizard.Wizard;

public class EntityWizard extends Wizard {

	private List<EntityWizardInput> entities;

	private EntitySelectionWizardPage entitySelectionWizardPage;
	
	private TagDescriptorSelectionWizardPage tagDescriptionSelectionWizardPage;
	
	private ViewFolderSelectionWizardPage viewFolderSelectionWizardPage;

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
		tagDescriptionSelectionWizardPage = new TagDescriptorSelectionWizardPage();
		viewFolderSelectionWizardPage = new ViewFolderSelectionWizardPage();
		
		addPage(entitySelectionWizardPage);
		addPage(tagDescriptionSelectionWizardPage);
		addPage(viewFolderSelectionWizardPage);
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
		
		InputStream is = null;
		File file = tagDescriptionSelectionWizardPage.getSelectedFile();
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ITagTreeProvider tagFactory = new TagTreeParser(is);

		ViewEngine engine = ViewEngine.getInstance();
		engine.generateViews(entityModel, tagFactory);

		saveView(engine.getStreams());
		return true;
	}
	
	private void saveView(List<OutputStream> streams) {
		
		for (OutputStream os : streams) {
			System.out.println(os);
		}		
	}

}
