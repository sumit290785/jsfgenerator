package jsfgenerator.ui.wizards;

import jsfgenerator.ui.composites.ResourceSelectionComposite;
import jsfgenerator.ui.model.ProjectResourceProvider;
import jsfgenerator.ui.providers.ResourceLabelProvider;
import jsfgenerator.ui.providers.ResourceSelectionContentProvider;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetProjectCreationDataModelProperties;
import org.eclipse.jst.servlet.ui.project.facet.WebProjectWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;

public class EJBProjectSelectionWizardPage extends AbstractFacetWizardPage {

	public static class ProjectViewerFilter extends ViewerFilter {
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof IProject && ((IProject) element).isOpen()) {
				if (ProjectResourceProvider.isEjbProject((IProject) element)) {
					return true;
				}
				return false;
			}

			return false;
		}

	}

	private JSFGenInstallConfig config;

	protected ResourceSelectionComposite resourceComposite;

	protected IFolder selectedFolder;

	public EJBProjectSelectionWizardPage() {
		super("EJBProjectSelectionWizardPage");
		setTitle("Enterprise java bean project selection");
		setDescription("Please, select the EJB project of the model");
	}

	public void setConfig(final Object config) {
		this.config = (JSFGenInstallConfig) config;
	}

	public void createControl(Composite parent) {
		resourceComposite = new ResourceSelectionComposite(parent, SWT.NONE, new ResourceLabelProvider(),
				new ResourceSelectionContentProvider(), new ProjectViewerFilter(), ResourcesPlugin.getWorkspace().getRoot()
						.getProjects());
		setControl(resourceComposite);

		resourceComposite.getFilteredTree().getViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				TreeSelection selection = (TreeSelection) event.getSelection();
				if (selection != null && selection.getFirstElement() != null) {
					IProject project = (IProject) selection.getFirstElement();
					config.setEjbProject(project);
					resourceComposite.getSelectionText().setText(project.getFullPath().toPortableString());
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
		WebProjectWizard wpw = (WebProjectWizard) getWizard();
		if (!wpw.getDataModel().getBooleanProperty(IJ2EEFacetProjectCreationDataModelProperties.ADD_TO_EAR)) {
			setErrorMessage("It is required to select an EAR project on the first page of the wizard");
		} else if (config.getEjbProject() == null) {
			setErrorMessage("Please, select an EJB project with the model for the generation");
		} else {
			setErrorMessage(null);
		}

		setPageComplete(getErrorMessage() == null);
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			validate();
		}
		super.setVisible(visible);
	}

	public IFolder getSelectedFolder() {
		return selectedFolder;
	}

}
