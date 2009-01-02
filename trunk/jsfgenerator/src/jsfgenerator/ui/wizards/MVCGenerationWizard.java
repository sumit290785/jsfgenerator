package jsfgenerator.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jsfgenerator.entitymodel.EntityModel;
import jsfgenerator.entitymodel.EntityModelBuilder;
import jsfgenerator.entitymodel.pageelements.EntityRelationship;
import jsfgenerator.entitymodel.pages.AbstractPageModel;
import jsfgenerator.entitymodel.pages.EntityListPageModel;
import jsfgenerator.entitymodel.pages.EntityPageModel;
import jsfgenerator.generation.common.GenerationException;
import jsfgenerator.generation.common.ViewAndControllerDTO;
import jsfgenerator.generation.common.ViewAndControllerEngine;
import jsfgenerator.generation.common.treebuilders.ResourceBundleBuilder;
import jsfgenerator.generation.common.utilities.ClassNameUtils;
import jsfgenerator.generation.common.utilities.NodeNameUtils;
import jsfgenerator.generation.controller.nodes.ControllerNodeFactory;
import jsfgenerator.generation.view.IViewTemplateProvider;
import jsfgenerator.generation.view.impl.ViewTemplateParser;
import jsfgenerator.ui.artifacthandlers.ArtifactEditHandler;
import jsfgenerator.ui.model.AbstractEntityDescriptionWrapper;
import jsfgenerator.ui.model.AbstractEntityFieldDescriptionWrapper;
import jsfgenerator.ui.model.EntityDescription;
import jsfgenerator.ui.model.EntityDescriptionEntityPageWrapper;
import jsfgenerator.ui.model.EntityDescriptionListPageWrapper;
import jsfgenerator.ui.model.EntityFieldDescription;
import jsfgenerator.ui.model.EntityFieldDescriptionEntityPageWrapper;
import jsfgenerator.ui.model.EntityFieldDescriptionListPageWrapper;
import jsfgenerator.ui.model.ProjectResourceProvider;
import jsfgenerator.ui.wizards.wizardpages.ControllerTargetPackageSelectionWizardPage;
import jsfgenerator.ui.wizards.wizardpages.EntityClassSelectionWizardPage;
import jsfgenerator.ui.wizards.wizardpages.EntityListPageFieldSelectionWizardPage;
import jsfgenerator.ui.wizards.wizardpages.EntityPageFieldSelectionWizardPage;
import jsfgenerator.ui.wizards.wizardpages.TagDescriptorSelectionWizardPage;
import jsfgenerator.ui.wizards.wizardpages.ViewTargetFolderSelectionWizardPage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.PlatformUI;

public class MVCGenerationWizard extends Wizard {

	private List<EntityDescriptionEntityPageWrapper> entityDescriptionEntityPageWrappers;
	private List<EntityDescriptionListPageWrapper> entityDescriptionListPageWrappers;

	private TagDescriptorSelectionWizardPage tagDescriptorSelectionWizardPage;
	private ViewTargetFolderSelectionWizardPage viewTargetFolderSelectionWizardPage;
	private ControllerTargetPackageSelectionWizardPage controllerTargetPackageSelectionWizardPage;
	private EntityClassSelectionWizardPage entityClassSelectionWizardPage;
	private EntityPageFieldSelectionWizardPage entityPageFieldSelectionWizardPage;
	private EntityListPageFieldSelectionWizardPage entityListPageFieldSelectionWizardPage;

	public MVCGenerationWizard(List<EntityDescription> entityDescriptions) {
		super();
		this.entityDescriptionEntityPageWrappers = EntityDescriptionEntityPageWrapper.createWrappers(entityDescriptions);
		this.entityDescriptionListPageWrappers = EntityDescriptionListPageWrapper.createWrappers(entityDescriptions);
		setWindowTitle("View and Controller generation wizard");

		Image img = new Image(PlatformUI.getWorkbench().getDisplay(), getClass().getResourceAsStream(
				"/resource/images/applications-system40.png"));

		setDefaultPageImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	@Override
	public void addPages() {
		entityClassSelectionWizardPage = new EntityClassSelectionWizardPage();
		tagDescriptorSelectionWizardPage = new TagDescriptorSelectionWizardPage();
		entityPageFieldSelectionWizardPage = new EntityPageFieldSelectionWizardPage();
		entityListPageFieldSelectionWizardPage = new EntityListPageFieldSelectionWizardPage();
		viewTargetFolderSelectionWizardPage = new ViewTargetFolderSelectionWizardPage();
		controllerTargetPackageSelectionWizardPage = new ControllerTargetPackageSelectionWizardPage();

		addPage(entityClassSelectionWizardPage);
		addPage(tagDescriptorSelectionWizardPage);
		addPage(entityPageFieldSelectionWizardPage);
		addPage(entityListPageFieldSelectionWizardPage);
		addPage(viewTargetFolderSelectionWizardPage);
		addPage(controllerTargetPackageSelectionWizardPage);
		super.addPages();
	}

	@Override
	public boolean performFinish() {

		try {
			getContainer().run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

					monitor.beginTask("JSF View and Controller generation", 4);
					monitor.subTask("Building the model");

					EntityModelBuilder builder = new EntityModelBuilder();

					for (EntityDescriptionEntityPageWrapper entityWrapper : getEntityDescriptionEntityPageWrappers()) {

						if (entityWrapper.isPageGenerated()) {
							String viewId = entityWrapper.getViewId();
							if (!builder.isViewSpecified(viewId)) {
								builder.createEntityPageModel(viewId, entityWrapper.getEntityDescription().getEntityClassName());
							}

							builder.addEntityForm(viewId, entityWrapper.getEntityDescription(), null);

							for (EntityFieldDescription entityField : getSingleEmbeddedFields(entityWrapper)) {
								AbstractEntityFieldDescriptionWrapper fieldWrapper = entityWrapper.getFieldWrapper(entityField);
								builder
										.addEntityForm(viewId, fieldWrapper.getEntityWrapper().getEntityDescription(),
												entityField);
							}

							for (EntityFieldDescriptionEntityPageWrapper entityFieldWrapper : getMultiplembeddedFields(entityWrapper)) {
								builder.addEntityListForm(entityWrapper.getViewId(), entityWrapper.getEntityDescription(),
										entityFieldWrapper.getEntityFieldDescription(), entityFieldWrapper
												.getEntityDescriptionWrapper().getEntityDescription());
							}
						}
					}

					for (EntityDescriptionListPageWrapper entityWrapper : getEntityDescriptionListPageWrappers()) {
						if (entityWrapper.isPageGenerated()) {
							String viewId = entityWrapper.getViewId();
							if (!builder.isViewSpecified(viewId)) {
								builder.createEntityListPageModel(viewId, entityWrapper.getEntityDescription()
										.getEntityClassName());
							}

							for (AbstractEntityFieldDescriptionWrapper entityFieldWrapper : entityWrapper.getFieldWrappers()) {
								EntityFieldDescriptionListPageWrapper wrapper = (EntityFieldDescriptionListPageWrapper) entityFieldWrapper;
								if (wrapper.isShown()
										&& (wrapper.getEntityFieldDescription().getRelationshipToEntity() == null || wrapper
												.getEntityFieldDescription().getRelationshipToEntity().equals(
														EntityRelationship.FIELD))) {

									builder.addFieldToList(viewId, entityWrapper.getEntityDescription(), wrapper
											.getEntityFieldDescription(), null, null);
								} else if (wrapper.isShown()) {

									List<String> genericParams = ClassNameUtils.getGenericParameterList(wrapper
											.getEntityFieldDescription().getClassName());
									if (genericParams.size() > 0) {
										String referencedEntityClassName = genericParams.get(0);

										builder.addFieldToList(viewId, entityWrapper.getEntityDescription(), wrapper
												.getEntityFieldDescription(), referencedEntityClassName, wrapper.getFieldName());
									}
								}
							}
						}
					}

					monitor.subTask("Building the model");
					monitor.worked(1);
					EntityModel entityModel = builder.createEntityModel();
					InputStream is = null;
					File file = tagDescriptorSelectionWizardPage.getSelectedFile();
					try {
						is = new FileInputStream(file);
					} catch (FileNotFoundException e) {
					}

					monitor.worked(1);
					monitor.subTask("Load selected descriptor file");

					IViewTemplateProvider tagFactory = new ViewTemplateParser(is);

					IPackageFragment fragment = controllerTargetPackageSelectionWizardPage.getSelectedPackageFragment();
					ControllerNodeFactory controllerNodeProvider = ControllerNodeFactory.getInstance();
					controllerNodeProvider.setPackageName(fragment.getElementName());

					monitor.worked(1);
					monitor.subTask("Generate views and controllers");

					ViewAndControllerEngine engine = ViewAndControllerEngine.getInstance();
					engine.generateViewsAndControllers(entityModel, tagFactory, controllerNodeProvider);

					monitor.subTask("Save classes and resources");
					for (AbstractPageModel pageModel : entityModel.getPageModels()) {

						if (pageModel instanceof EntityPageModel) {
							ViewAndControllerDTO viewDTO = engine.getViewAndControllerDTO(pageModel.getViewId());
							saveView(pageModel.getViewId(), viewDTO.getViewStream());
							String fileName = NodeNameUtils.getEntityPageClassFileNameByUniqueName(pageModel.getViewId());
							saveController(fileName, viewDTO.getViewClass());
							saveResourceBundles();

							addManagedBeanToFacesConfig(pageModel.getViewId(), viewDTO.getControllerClassName(), monitor);
						} else if (pageModel instanceof EntityListPageModel) {
							String fileName = NodeNameUtils.getListPageClassFileNameByUniqueName(pageModel.getViewId());
							// TODO: list
						}
					}

					monitor.done();
				}

			});
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		return true;
	}

	public List<String> getInputTagIds() {
		if (tagDescriptorSelectionWizardPage != null && tagDescriptorSelectionWizardPage.getSelectedFile() != null) {
			InputStream is = null;
			File file = tagDescriptorSelectionWizardPage.getSelectedFile();
			try {
				is = new FileInputStream(file);
			} catch (FileNotFoundException e) {
			}

			IViewTemplateProvider tagFactory = new ViewTemplateParser(is);
			return tagFactory.getInputTagNames();
		}

		return null;
	}

	private void saveController(String fileName, CompilationUnit controller) {
		String sourceCode = formatCode(controller.toString());
		IPackageFragment fragment = controllerTargetPackageSelectionWizardPage.getSelectedPackageFragment();

		ICompilationUnit cu = null;
		try {

			// delete the file if exists
			IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(fragment.getPath());
			delete(folder, fileName);

			cu = fragment.createCompilationUnit(fileName, sourceCode, false, null);
			cu.becomeWorkingCopy(null);
		} catch (JavaModelException e) {
			throw new GenerationException(
					"Could not save the generated controller! Source is on the disk, but it is not in the workspace.", e);
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

	private void saveResourceBundles() {
		InputStream stream = ResourceBundleBuilder.getInstance().getMessageInputStream();

		IFolder srcFolder = ProjectResourceProvider.getInstance().getJsfProject().getFolder("src");
		String fileNameEN = NodeNameUtils.getResourceBundleName();

		delete(srcFolder, fileNameEN);
		try {
			IFile fileEN = srcFolder.getFile(fileNameEN);
			fileEN.create(stream, IResource.FORCE, null);
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
			throw new RuntimeException("Exception at formating the generated code format", e);
		} catch (BadLocationException e) {
			throw new RuntimeException("Exception at formating the generated code format", e);
		}

		return document.get();
	}

	private List<EntityFieldDescriptionEntityPageWrapper> getMultiplembeddedFields(
			AbstractEntityDescriptionWrapper entityDescriptionWrapper) {
		List<EntityFieldDescriptionEntityPageWrapper> entityFields = new ArrayList<EntityFieldDescriptionEntityPageWrapper>();

		for (AbstractEntityFieldDescriptionWrapper entityFieldWrapper : entityDescriptionWrapper.getFieldWrappers()) {
			EntityRelationship rel = entityFieldWrapper.getEntityFieldDescription().getRelationshipToEntity();
			if (entityFieldWrapper.getEntityDescriptionWrapper() != null
					&& (EntityRelationship.ONE_TO_MANY.equals(rel) || EntityRelationship.MANY_TO_MANY.equals(rel))) {
				entityFields.add((EntityFieldDescriptionEntityPageWrapper) entityFieldWrapper);
			}
		}

		return entityFields;
	}

	private List<EntityFieldDescription> getSingleEmbeddedFields(AbstractEntityDescriptionWrapper entityDescriptionWrapper) {
		List<EntityFieldDescription> entityFields = new ArrayList<EntityFieldDescription>();

		for (AbstractEntityFieldDescriptionWrapper entityFieldWrapper : entityDescriptionWrapper.getFieldWrappers()) {
			EntityRelationship rel = entityFieldWrapper.getEntityFieldDescription().getRelationshipToEntity();
			if (entityFieldWrapper.getEntityDescriptionWrapper() != null
					&& (EntityRelationship.EMBEDDED.equals(rel) || EntityRelationship.MANY_TO_ONE.equals(rel) || EntityRelationship.ONE_TO_ONE
							.equals(rel))) {
				entityFields.add(entityFieldWrapper.getEntityFieldDescription());
			}
		}

		return entityFields;
	}

	private void addManagedBeanToFacesConfig(String viewId, String className, IProgressMonitor monitor) {
		ArtifactEditHandler handler = ArtifactEditHandler.getInstance();
		handler.setMonitor(monitor);
		handler.addManagedBeanToFacesConfig(viewId, className);
	}

	@Override
	public boolean needsProgressMonitor() {
		return true;
	}

	public List<EntityDescriptionEntityPageWrapper> getEntityDescriptionEntityPageWrappers() {
		return entityDescriptionEntityPageWrappers;
	}

	public List<EntityDescriptionListPageWrapper> getEntityDescriptionListPageWrappers() {
		return entityDescriptionListPageWrappers;
	}

	public String validateViewId() {
		Set<String> ids = new HashSet<String>();
		List<AbstractEntityDescriptionWrapper> descriptors = new ArrayList<AbstractEntityDescriptionWrapper>();
		descriptors.addAll(entityDescriptionEntityPageWrappers);
		descriptors.addAll(entityDescriptionListPageWrappers);
		for (AbstractEntityDescriptionWrapper wrapper : descriptors) {
			String id = wrapper.getViewId();
			if (id != null && !id.equals("") && ids.contains(id)) {
				return id + " is not unique view id!";
			}
			ids.add(id);
		}

		return null;
	}

}
