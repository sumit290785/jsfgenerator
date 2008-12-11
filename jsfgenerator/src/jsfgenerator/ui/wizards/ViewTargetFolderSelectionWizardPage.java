package jsfgenerator.ui.wizards;

import jsfgenerator.ui.composites.ResourceSelectionComposite;
import jsfgenerator.ui.providers.ResourceLabelProvider;
import jsfgenerator.ui.providers.ResourceSelectionContentProvider;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Wizard page implementation for selecting the target folder which is used to
 * generate the view files into.
 * 
 * @author zoltan verebes
 * 
 */
public class ViewTargetFolderSelectionWizardPage extends WizardPage {

	/**
	 * Filters out all of the non-container (folder and project) type elements
	 * of the tree viewer content
	 * 
	 * @author zoltan verebes
	 * 
	 */
	public static class FolderViewerFilter extends ViewerFilter {
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof IContainer) {
				return true;
			}

			return false;
		}

	}

	protected ResourceSelectionComposite resourceComposite;

	protected IFolder selectedFolder;

	protected ViewTargetFolderSelectionWizardPage() {
		super("ViewFolderSelectionWizardPage");
		setTitle("View folder page");
		setDescription("Please, select a target folder for the generated view xhtml files");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent) {
		resourceComposite = new ResourceSelectionComposite(parent, SWT.NONE, new ResourceLabelProvider(),
				new ResourceSelectionContentProvider(), new FolderViewerFilter());
		setControl(resourceComposite);
		
		resourceComposite.getFilteredTree().getViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				TreeSelection selection = (TreeSelection) event.getSelection();
				if (selection.getFirstElement() instanceof IFolder) {
					IFolder folder = (IFolder) selection.getFirstElement();
					selectedFolder = folder;
					resourceComposite.getSelectionText().setText(folder.getFullPath().toPortableString());
				} else {
					selectedFolder = null;
					resourceComposite.getSelectionText().setText("");
				}

				validate();
			}

		});
		validate();
	}

	protected void validate() {
		if (resourceComposite.getSelectionText().getText() == null
				|| resourceComposite.getSelectionText().getText().equals("")) {
			setErrorMessage("Please, select a folder");
		} else {
			setErrorMessage(null);
		}
		
		setPageComplete(getErrorMessage() == null);
	}

	public IFolder getSelectedFolder() {
		return selectedFolder;
	}

}
