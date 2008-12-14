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
import jsfgenerator.entitymodel.forms.EntityRelationship;
import jsfgenerator.entitymodel.impl.ASTEntityModelBuilder;
import jsfgenerator.entitymodel.pages.AbstractPageModel;
import jsfgenerator.generation.common.GenerationException;
import jsfgenerator.generation.common.ViewAndControllerDTO;
import jsfgenerator.generation.common.ViewAndControllerEngine;
import jsfgenerator.generation.common.utilities.NodeNameUtils;
import jsfgenerator.generation.controller.nodes.ControllerNodeFactory;
import jsfgenerator.generation.view.ITagTreeProvider;
import jsfgenerator.generation.view.impl.TagTreeParser;
import jsfgenerator.ui.model.EntityDescription;
import jsfgenerator.ui.model.EntityFieldDescription;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
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

	public MVCGenerationWizard(List<EntityDescription> entityDescriptions) {
		super();
		this.entityDescriptions = entityDescriptions;
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
					builder.createEntityPageModel(viewId, entity.getEntityClassName());
				}

				// if (hasSimpleField(entity)) {
				builder.addSimpleEntityForm(viewId, entity, null);
				// }

				for (EntityFieldDescription entityField : getSimpleEmbeddedFields(entity)) {
					builder.addSimpleEntityForm(viewId, entityField.getEntityDescription(), entityField);
				}

				for (EntityFieldDescription entityField : getComplexEmbeddedFields(entity)) {
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

		IPackageFragment fragment = controllerTargetPackageSelectionWizardPage.getSelectedPackageFragment();
		ControllerNodeFactory controllerNodeProvider = ControllerNodeFactory.getInstance();
		controllerNodeProvider.setPackageName(fragment.getElementName());

		ViewAndControllerEngine engine = ViewAndControllerEngine.getInstance();
		engine.generateViewsAndControllers(entityModel, tagFactory, controllerNodeProvider);

		for (AbstractPageModel pageModel : entityModel.getPageModels()) {
			ViewAndControllerDTO viewDTO = engine.getViewAndControllerDTO(pageModel.getViewId());
			saveView(pageModel.getViewId(), viewDTO.getViewStream());
			saveController(pageModel.getViewId(), viewDTO.getViewClass());
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

	private void saveController(String className, CompilationUnit controller) {
		String sourceCode = formatCode(controller.toString());
		IPackageFragment fragment = controllerTargetPackageSelectionWizardPage.getSelectedPackageFragment();

		ICompilationUnit cu = null;
		try {
			String fileName = NodeNameUtils.getEntityPageClassFileNameByUniqueName(className);

			// delete the file if exists
			IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(fragment.getPath());
			delete(folder, fileName);

			cu = fragment.createCompilationUnit(fileName, sourceCode, false, null);
			cu.becomeWorkingCopy(null);
		} catch (JavaModelException e) {
			throw new GenerationException("Could not save the generated controller!", e);
		}
	}

	private void saveView(String viewId, ByteArrayOutputStream stream) {
		IFolder folder = viewTargetFolderSelectionWizardPage.getSelectedFolder();
		if (folder == null) {
			throw new NullPointerException("Target folder is not selected");
		}

		if (!folder.exists()) {
			throw new NullPointerException("Target folder does not exist!");
		}

		String fileName = NodeNameUtils.getEntityPageViewNameByUniqueName(viewId);
		delete(folder, fileName);

		InputStream is = new ByteArrayInputStream(stream.toByteArray());
		try {
			IFile file = folder.getFile(fileName);
			file.create(is, IResource.FORCE, null);
		} catch (CoreException e) {
			throw new GenerationException("Could not save the generated view!", e);
		}

	}

	private void delete(IFolder folder, String fileName) {
		IFile file = folder.getFile(fileName);
		if (file.exists()) {
			try {
				file.delete(IResource.FORCE, null);
			} catch (CoreException e) {
				throw new GenerationException("Could not delete the previous version of the view! File name: " + fileName, e);
			}
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

	private List<EntityFieldDescription> getComplexEmbeddedFields(EntityDescription entityDescription) {
		List<EntityFieldDescription> entityFields = new ArrayList<EntityFieldDescription>();

		for (EntityFieldDescription entityFieldDescription : entityDescription.getEntityFieldDescriptions()) {
			if (entityFieldDescription.getEntityDescription() != null
					&& (EntityRelationship.ONE_TO_MANY.equals(entityFieldDescription.getRelationshipToEntity()) || EntityRelationship.MANY_TO_MANY
							.equals(entityFieldDescription.getRelationshipToEntity()))) {
				entityFields.add(entityFieldDescription);
			}
		}

		return entityFields;
	}

	private List<EntityFieldDescription> getSimpleEmbeddedFields(EntityDescription entityDescription) {
		List<EntityFieldDescription> entityFields = new ArrayList<EntityFieldDescription>();

		for (EntityFieldDescription entityFieldDescription : entityDescription.getEntityFieldDescriptions()) {
			if (entityFieldDescription.getEntityDescription() != null
					&& (EntityRelationship.EMBEDDED.equals(entityFieldDescription.getRelationshipToEntity())
							|| EntityRelationship.ONE_TO_ONE.equals(entityFieldDescription.getRelationshipToEntity()) || EntityRelationship.MANY_TO_ONE
							.equals(entityFieldDescription.getRelationshipToEntity()))) {
				entityFields.add(entityFieldDescription);
			}
		}

		return entityFields;
	}

}
