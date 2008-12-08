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
import jsfgenerator.ui.wizards.EntityWizardInput.EntityFieldInput;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IRegion;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class EntityWizard extends Wizard {
	
	private IJavaProject project;
	
	private IRegion region;

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
		initGeneration();
		
		ASTEntityModelBuilder builder = new ASTEntityModelBuilder();

		for (EntityWizardInput entity : entitySelectionWizardPage.getSelectedEntities()) {
			String viewId = entity.getName();
			if (!builder.isViewSpecified(viewId)) {
				builder.createEntityPageModel(viewId);
			}

			if (hasSimpleField(entity)) {
				builder.addSimpleEntityForm(viewId, entity);
			}
			
			//TODO
			for (EntityFieldInput fieldInput : getComplexFields(entity)) {
				Type inputFieldType = fieldInput.getFieldType();
				if (inputFieldType.isParameterizedType() && ((ParameterizedType) inputFieldType).typeArguments().size() == 1) {
					ParameterizedType ptype = (ParameterizedType) inputFieldType;
					Type param = (Type)ptype.typeArguments().get(0);
					builder.addComplexEntityFormList(viewId, entity, fieldInput, entity);
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

	private void initGeneration() {
		IFolder folder = viewFolderSelectionWizardPage.getSelectedFolder();
		project = JavaCore.create(folder.getProject());
		buildRegion();
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
		final CodeFormatter formatter = ToolFactory.createCodeFormatter(JavaCore.getDefaultOptions());
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
	
	/**
	 * TODO: speed it up and change the static java.util.List to any type
	 * @param input
	 * @return
	 */
	private List<EntityFieldInput> getComplexFields(EntityWizardInput input) {
		List<EntityFieldInput> inputs = new ArrayList<EntityFieldInput>();
		
		for (EntityFieldInput fieldInput : input.getFields()) {
			
			//TODO: speed it up, get the fully qualified name from the imports!
			try {
				//TODO: get the fully qualified name of the particular class and check whether it is instance of java.util.Collection
				IType type = project.findType("java.util.List");
				//ITypeHierarchy hierarchy = project.newTypeHierarchy(type, region, null);
				//hierarchy.getSuperInterfaces(type);
			} catch (JavaModelException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			Type type = fieldInput.getFieldType();
			if (fieldInput.getFieldType().toString().startsWith("List<")) {
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
	
	//TODO: speed this up!!!!
	protected void buildRegion() {
		region = JavaCore.newRegion();
		/*
		 * add all of the classes and jars in the project
		 */
		try {
			for (IPackageFragmentRoot root : project.getAllPackageFragmentRoots()) {
				region.add(root);
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
