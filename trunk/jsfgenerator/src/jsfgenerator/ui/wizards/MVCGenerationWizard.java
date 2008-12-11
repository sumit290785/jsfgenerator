package jsfgenerator.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jsfgenerator.entitymodel.EntityModel;
import jsfgenerator.entitymodel.impl.ASTEntityModelBuilder;
import jsfgenerator.entitymodel.pages.AbstractPageModel;
import jsfgenerator.generation.common.ViewAndControllerDTO;
import jsfgenerator.generation.common.ViewAndControllerEngine;
import jsfgenerator.generation.controller.AbstractControllerNodeProvider;
import jsfgenerator.generation.controller.nodes.ControllerNodeFactory;
import jsfgenerator.generation.view.ITagTreeProvider;
import jsfgenerator.generation.view.impl.TagTreeParser;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class MVCGenerationWizard extends Wizard {
	private List<EntityDescription> entityDescriptions;

	private TagDescriptorSelectionWizardPage tagDescriptorSelectionWizardPage;
	private ViewTargetFolderSelectionWizardPage viewTargetFolderSelectionWizardPage;
	private ControllerTargetPackageSelectionWizardPage controllerTargetPackageSelectionWizardPage;
	private EntityClassSelectionWizardPage entityClassSelectionWizardPage;
	private EntityClassFieldSelectionWizardPage entityClassFieldSelectionWizardPage;

	private IJavaProject project;

	public MVCGenerationWizard(IJavaProject project, List<EntityDescription> entityDescriptions) {
		super();
		this.entityDescriptions = entityDescriptions;
		this.project = project;
		setWindowTitle("View and Controller generation wizard");
	}

	@Override
	public void addPages() {
		entityClassSelectionWizardPage = new EntityClassSelectionWizardPage();
		tagDescriptorSelectionWizardPage = new TagDescriptorSelectionWizardPage();
		entityClassFieldSelectionWizardPage = new EntityClassFieldSelectionWizardPage();
		viewTargetFolderSelectionWizardPage = new ViewTargetFolderSelectionWizardPage();
		controllerTargetPackageSelectionWizardPage = new ControllerTargetPackageSelectionWizardPage();

		addPage(entityClassSelectionWizardPage);
		addPage(tagDescriptorSelectionWizardPage);
		addPage(entityClassFieldSelectionWizardPage);
		addPage(viewTargetFolderSelectionWizardPage);
		addPage(controllerTargetPackageSelectionWizardPage);
		super.addPages();
	}

	@Override
	public boolean performFinish() {

		ASTEntityModelBuilder builder = new ASTEntityModelBuilder();

		for (EntityDescription entity : entityDescriptions) {

			if (entity.isEntityPage()) {
				String viewId = entity.getViewId();
				if (!builder.isViewSpecified(viewId)) {
					builder.createEntityPageModel(viewId);
				}

				//if (hasSimpleField(entity)) {
					builder.addSimpleEntityForm(viewId, entity);
				//}

				for (EntityFieldDescription entityField : getComplexFields(entity)) {
					builder.addComplexEntityFormList(entity, entityField);
				}
			}
		}

		EntityModel entityModel = builder.createEntityModel();
		InputStream is = null;
		File file = tagDescriptorSelectionWizardPage.getSelectedFile();
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

	public List<EntityDescription> getEntityDescriptions() {
		return entityDescriptions;
	}

	public List<String> getInputTagIds() {
		if (tagDescriptorSelectionWizardPage != null && tagDescriptorSelectionWizardPage.getSelectedFile() != null) {
			InputStream is = null;
			File file = tagDescriptorSelectionWizardPage.getSelectedFile();
			try {
				is = new FileInputStream(file);
			} catch (FileNotFoundException e) {
			}

			ITagTreeProvider tagFactory = new TagTreeParser(is);
			return tagFactory.getInputTagIds();
		}

		return null;
	}

	public IJavaProject getProject() {
		return project;
	}

	private void saveController(String className, CompilationUnit controller) {
		String sourceCode = formatCode(controller.toString());

		IPackageFragment fragment = null;
		try {
			fragment = (project.getAllPackageFragmentRoots()[0]).getPackageFragment("pkg.generated");
		} catch (JavaModelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ICompilationUnit cu = null;
		try {
			cu = fragment.createCompilationUnit(className + ".java", sourceCode, false, null);
			cu.becomeWorkingCopy(null);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private String formatCode(String source) {
		final CodeFormatter formatter = ToolFactory.createCodeFormatter(JavaCore.getOptions());
		TextEdit edit = formatter.format(CodeFormatter.K_COMPILATION_UNIT, source, 0, source.length(), 0, null);
		IDocument document = new Document(source);
		try {
			edit.apply(document);
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		return document.get();
	}

	private void saveView(String viewId, ByteArrayOutputStream stream) {
		IFolder folder = viewTargetFolderSelectionWizardPage.getSelectedFolder();
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
	
	private List<EntityFieldDescription> getComplexFields(EntityDescription entityDescription) {
		List<EntityFieldDescription> entityFields = new ArrayList<EntityFieldDescription>();
		
		for (EntityFieldDescription entityFieldDescription : entityDescription.getEntityFieldDescriptions()) {
			if (entityFieldDescription.isCollectionInComplexForm()) {
				entityFields.add(entityFieldDescription);
			}
		}
		
		return entityFields;
	}

}
