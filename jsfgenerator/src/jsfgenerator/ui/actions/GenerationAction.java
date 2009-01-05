package jsfgenerator.ui.actions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jsfgenerator.ui.astvisitors.EntityClassParser;
import jsfgenerator.ui.model.EntityDescription;
import jsfgenerator.ui.utilities.ProjectResourceProvider;
import jsfgenerator.ui.wizards.MVCGenerationWizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * This action is called when the user selects the view and controller generation in the context menu. It shows up the wizard
 * 
 * @author zoltan verebes
 * 
 */
public class GenerationAction extends Action implements IObjectActionDelegate {

	private IWorkbenchPart part;

	private StructuredSelection selection;

	private IProject selectedEjbProject;

	public void setActivePart(IAction action, IWorkbenchPart part) {
		this.part = part;
	}

	public void run(IAction action) {

		Set<IFile> resources = collectSelectedFiles();

		if (selectedEjbProject != null && resources.size() != 0) {

			try {
				ProjectResourceProvider.getInstance().findProjectsByEjbProject(selectedEjbProject);
			} catch (Exception e) {
				ErrorDialog errorDialog = new ErrorDialog(part.getSite().getShell(), "Generation error", e.getMessage(),
						new Status(IStatus.INFO, "jsfgenerator",
								"Related projects not found in the workspace. An EAR and a Web project are required", e),
						IStatus.INFO);
				errorDialog.open();
			}

			List<EntityDescription> entityDescriptions = EntityClassParser.findEntities(resources.toArray(new IFile[0]));

			WizardDialog dialog = new WizardDialog(part.getSite().getShell(), new MVCGenerationWizard(entityDescriptions));
			dialog.setMinimumPageSize(800, 550);
			dialog.open();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {

		if (selection instanceof StructuredSelection) {
			this.selection = (StructuredSelection) selection;
		} else {
			this.selection = null;
		}
	}

	private Set<IFile> collectSelectedFiles() {
		Set<IFile> files = new HashSet<IFile>();

		if (selection == null) {
			return files;
		}

		Iterator<?> it = selection.iterator();
		while (it.hasNext()) {
			Object element = it.next();

			if (!(element instanceof IResource)) {
				continue;
			}

			IProject project = ((IResource) element).getProject();
			if (!ProjectResourceProvider.isEjbProject(project)) {
				continue;
			}

			selectedEjbProject = ((IResource) element).getProject();
			files.addAll(getFiles((IResource) element));
		}

		return files;
	}

	private Set<IFile> getFiles(IResource resource) {
		Set<IFile> files = new HashSet<IFile>();
		if (resource == null) {
			return files;
		}

		if (resource instanceof IFile) {
			files.add((IFile) resource);
			return files;
		}

		if (resource instanceof IFolder) {
			try {
				for (IResource member : ((IFolder) resource).members()) {
					files.addAll(getFiles(member));
				}
			} catch (CoreException e) {
			}
		}

		if (resource instanceof IProject) {
			try {
				for (IResource member : ((IProject) resource).members()) {
					files.addAll(getFiles(member));
				}
			} catch (CoreException e) {
			}

		}

		return files;
	}
}
