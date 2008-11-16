package jsfgenerator.ui.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import jsfgenerator.generation.common.ViewEngine;
import jsfgenerator.generation.controller.nodes.ControllerNodeFactory;
import jsfgenerator.generation.controller.nodes.IControllerNodeProvider;
import jsfgenerator.generation.view.ITagTreeProvider;
import jsfgenerator.generation.view.impl.TagTreeParser;
import jsfgenerator.inspector.entitymodel.AbstractEntityModelBuilder;
import jsfgenerator.inspector.entitymodel.EntityModel;
import jsfgenerator.inspector.entitymodel.impl.ASTEntityModelBuilder;
import jsfgenerator.inspector.entitymodel.pages.PageModel;

import org.eclipse.jdt.core.dom.CompilationUnit;
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
		}
		
		ITagTreeProvider tagFactory = new TagTreeParser(is);
		//TODO
		IControllerNodeProvider controllerNodeProvider = new ControllerNodeFactory("selected.pkg");

		ViewEngine engine = ViewEngine.getInstance();
		engine.generateViewsAndControllers(entityModel, tagFactory, controllerNodeProvider);

		for (PageModel pageModel : entityModel.getPageModels()) {
			saveView(pageModel.getName(), engine.getView(pageModel.getName()));
			saveController(pageModel.getName(), engine.getController(pageModel.getName()));
		}
		
		return true;
	}
	
	private void saveController(String name, CompilationUnit controller) {
		//TODO
	}

	private void saveView(String viewId, OutputStream stream) {
		//TODO
	}

}
