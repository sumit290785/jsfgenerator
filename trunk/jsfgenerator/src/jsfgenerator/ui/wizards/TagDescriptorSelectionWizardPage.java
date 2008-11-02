package jsfgenerator.ui.wizards;

import java.io.File;
import java.util.Arrays;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

public class TagDescriptorSelectionWizardPage extends WizardPage {

	private static class XMLFileLabelProvider extends LabelProvider {
		private static final Image IMG_FOLDER = PlatformUI.getWorkbench().getSharedImages().getImage(
				ISharedImages.IMG_OBJ_FOLDER);

		private static final Image IMG_FILE = PlatformUI.getWorkbench().getSharedImages().getImage(
				ISharedImages.IMG_OBJ_FILE);

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object element) {
			if (element instanceof IContainer) {
				return IMG_FOLDER;
			}

			if (element instanceof IFile) {
				return IMG_FILE;
			}

			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element) {
			if (element instanceof IContainer || element instanceof IFile) {
				return ((IResource) element).getName();
			}

			return super.getText(element);
		}
	}

	private static class XMLFileSelectionContentProvider implements ITreeContentProvider {
		private static final Object[] EMPTY = new Object[0];

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof IWorkspaceRoot) {
				IWorkspaceRoot root = (IWorkspaceRoot) parentElement;
				return root.getProjects();
			}

			if (parentElement instanceof IContainer) {
				IContainer container = (IContainer) parentElement;
				IResource[] members = null;
				try {
					members = container.members(IContainer.FOLDER | IContainer.FILE);
				} catch (CoreException e) {
					e.printStackTrace();
				}

				return members;
			}

			return EMPTY;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang
		 * .Object)
		 */
		public Object getParent(Object element) {
			if (element instanceof IResource) {
				return ((IResource) element).getParent();
			}

			return null;
		}

		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}

		public Object[] getElements(Object element) {
			return getChildren(element);
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	protected FilteredTree filteredTree;

	protected Text selectedFileText;

	protected IFile selectedFile;

	protected TagDescriptorSelectionWizardPage() {
		super("TagDescriptorSelectionWizardPage");
		setTitle("Tag description page");
		setDescription("This is a single page for selecting an xml file");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		setControl(composite);

		filteredTree = new FilteredTree(composite, SWT.BORDER, new PatternFilter());
		filteredTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		TreeViewer viewer = filteredTree.getViewer();
		viewer.setContentProvider(new XMLFileSelectionContentProvider());
		viewer.setLabelProvider(new XMLFileLabelProvider());
		viewer.setComparator(new ViewerComparator() {

			@Override
			public int category(Object element) {
				if (element instanceof IFolder) {
					return 1;
				}
				return 0;
			}

		});

		ViewerFilter filter = new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IContainer) {
					return true;
				}

				if (element instanceof IFile) {
					IFile file = (IFile) element;
					// is Xml file
					if (file.getFileExtension().equalsIgnoreCase("xml")) {
						return true;
					}
				}

				return false;
			}

		};

		viewer.setFilters(Arrays.asList(filter).toArray(new ViewerFilter[0]));

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		filteredTree.getViewer().setInput(root);

		final Label label = new Label(composite, SWT.NONE);
		label.setText("Selected file: ");
		selectedFileText = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		selectedFileText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				TreeSelection selection = (TreeSelection) event.getSelection();
				if (selection.getFirstElement() instanceof IFile) {
					IFile file = (IFile) selection.getFirstElement();
					selectedFile = file;
					selectedFileText.setText(file.getFullPath().toPortableString());
				} else {
					selectedFile = null;
					selectedFileText.setText("");
				}

				validate();
			}

		});
		validate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {
		return getErrorMessage() == null;
	}

	protected void validate() {
		if (selectedFileText.getText() == null || selectedFileText.getText().equals("")) {
			setErrorMessage("Please, select a file");
		} else {
			setErrorMessage(null);
		}
	}

	public File getSelectedFile() {
		if (selectedFile == null) {
			return null;
		}

		return selectedFile.getLocation().toFile();
	}

}
