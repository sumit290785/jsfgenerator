package jsfgenerator.ui.wizards.wizardpages;

import java.io.File;

import jsfgenerator.generation.common.INameConstants;
import jsfgenerator.ui.composites.ResourceSelectionComposite;
import jsfgenerator.ui.providers.ResourceLabelProvider;
import jsfgenerator.ui.providers.ResourceSelectionContentProvider;
import jsfgenerator.ui.utilities.ProjectResourceProvider;
import jsfgenerator.ui.validation.ViewTemplateValidator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO: validate the selected xml
 * 
 * @author zoltan verebes
 * 
 */
public class TagDescriptorSelectionWizardPage extends WizardPage {

	/**
	 * Filters all of the non container and non xml file type elements of the tree viewer content
	 * 
	 * @author zoltan verebes
	 * 
	 */
	public static class XMLViewerFilter extends ViewerFilter {
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof IContainer) {
				return true;
			}

			if (element instanceof IFile) {
				IFile file = (IFile) element;
				// is Xml file
				if (file.getFileExtension().equalsIgnoreCase(INameConstants.VIEW_XML_EXTENSION)) {
					return true;
				}
			}

			return false;
		}

	}

	protected ResourceSelectionComposite resourceComposite;

	protected IFile selectedFile;

	public TagDescriptorSelectionWizardPage() {
		super("TagDescriptorSelectionWizardPage");
		setTitle("Tag description selector page");
		setDescription("This page is to select a view descriptor xml file");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets .Composite)
	 */
	public void createControl(Composite parent) {
		resourceComposite = new ResourceSelectionComposite(parent, SWT.NONE, new ResourceLabelProvider(),
				new ResourceSelectionContentProvider(), new XMLViewerFilter(), ProjectResourceProvider.getInstance()
						.getJsfProject());
		setControl(resourceComposite);

		resourceComposite.getFilteredTree().getViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				TreeSelection selection = (TreeSelection) event.getSelection();
				if (selection.getFirstElement() instanceof IFile) {
					IFile file = (IFile) selection.getFirstElement();
					selectedFile = file;
					resourceComposite.getSelectionText().setText(file.getFullPath().toPortableString());
				} else {
					selectedFile = null;
					resourceComposite.getSelectionText().setText("");
				}

				validate();
			}

		});
		validate();
	}

	protected void validate() {
		
		if (selectedFile == null) {
			setErrorMessage("Please, select a file");
		} else {
			ViewTemplateValidator validator = new ViewTemplateValidator(selectedFile);
			validator.validate();
			
			if (!validator.validationPassed()) {
				setErrorMessage("Selected file is invalid! For details check the validation from the context menu for the selected file");
			} else {
				setErrorMessage(null);
			}
		}
		setPageComplete(getErrorMessage() == null);
	}

	public File getSelectedFile() {
		if (selectedFile == null) {
			return null;
		}

		return selectedFile.getLocation().toFile();
	}

}
