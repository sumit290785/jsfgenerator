package jsfgenerator.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jsfgenerator.entitymodel.AbstractEntityModelBuilder;
import jsfgenerator.entitymodel.EntityModel;
import jsfgenerator.entitymodel.impl.ASTEntityModelBuilder;
import jsfgenerator.entitymodel.pages.AbstractPageModel;
import jsfgenerator.generation.common.ViewAndControllerDTO;
import jsfgenerator.generation.common.ViewAndControllerEngine;
import jsfgenerator.generation.controller.AbstractControllerNodeProvider;
import jsfgenerator.generation.controller.nodes.ControllerNodeFactory;
import jsfgenerator.generation.view.ITagTreeProvider;
import jsfgenerator.generation.view.impl.TagTreeParser;
import jsfgenerator.ui.wizards.EntityWizardInput.EntityFieldInput;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.Type;
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

			if (hasSimpleField(entity)) {
				builder.addSimpleEntityForm(entity, viewId);
			}
			
			//TODO: get the generic entity and use that
			for (EntityFieldInput fieldInput : getComplexFields(entity)) {
				Type type = fieldInput.getFieldType();
				if (type.isParameterizedType() && ((ParameterizedType) type).typeArguments().size() == 1) {
					ParameterizedType ptype = (ParameterizedType) type;
					Type param = (Type)ptype.typeArguments().get(0);
					builder.addComplexEntityFormList(entity, viewId);
				}
				
			}
		}

		EntityModel entityModel = builder.createEntityModel();

		InputStream is = null;
		File file = tagDescriptionSelectionWizardPage.getSelectedFile();
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
		}

		ITagTreeProvider tagFactory = new TagTreeParser(is);
		// TODO: package
		AbstractControllerNodeProvider controllerNodeProvider = new ControllerNodeFactory("pkg.generated");

		ViewAndControllerEngine engine = ViewAndControllerEngine.getInstance();
		engine.generateViewsAndControllers(entityModel, tagFactory, controllerNodeProvider);

		for (AbstractPageModel pageModel : entityModel.getPageModels()) {
			ViewAndControllerDTO viewDTO = engine.getViewAndControllerDTO(pageModel.getViewId());
			saveView(viewDTO.getViewName(), viewDTO.getViewStream());
			saveController(viewDTO.getControllerClassName(), viewDTO.getViewClass());
		}

		return true;
	}

	private void saveController(String className, CompilationUnit controller) {
		IFolder folder = viewFolderSelectionWizardPage.getSelectedFolder();
		IJavaProject project = JavaCore.create(folder.getProject());

		IPackageFragment fragment = null;
		try {
			fragment = (project.getAllPackageFragmentRoots()[0]).getPackageFragment("pkg.generated");
		} catch (JavaModelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ICompilationUnit cu = null;
		try {
			cu = fragment.createCompilationUnit(className + ".java", controller.toString(), false, null);
			cu.becomeWorkingCopy(null);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

	}

	private void saveView(String viewId, ByteArrayOutputStream stream) {
		IFolder folder = viewFolderSelectionWizardPage.getSelectedFolder();
		if (folder == null) {
			throw new NullPointerException("Target folder is not selected");
		}

		if (!folder.exists()) {
			throw new NullPointerException("Target folder does not exist!");
		}

		String fileName = viewId + ".xhtml";
		IFile file = folder.getFile(fileName);
		if (file.exists()) {
			try {
				file.delete(IResource.FORCE, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		InputStream is = new ByteArrayInputStream(stream.toByteArray());
		try {
			file.create(is, IResource.FORCE, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private List<EntityFieldInput> getComplexFields(EntityWizardInput input) {
		
		List<EntityFieldInput> inputs = new ArrayList<EntityFieldInput>();
		
		for (EntityFieldInput fieldInput : input.getFields()) {
			
			if (fieldInput.getFieldType().isArrayType()) {
				inputs.add(fieldInput);
			}
		}
		
		return inputs;
	}
	
	private boolean hasSimpleField(EntityWizardInput input) {
		
		for (EntityFieldInput fieldInput : input.getFields()) {
			if (!fieldInput.getFieldType().isArrayType()) {
				return true;
			}
		}
		
		return false;
	}

}
