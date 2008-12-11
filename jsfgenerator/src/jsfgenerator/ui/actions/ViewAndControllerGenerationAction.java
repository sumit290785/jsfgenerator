package jsfgenerator.ui.actions;

import java.util.List;

import jsfgenerator.ui.astvisitors.EntityClassParser;
import jsfgenerator.ui.model.EntityDescription;
import jsfgenerator.ui.model.ProjectResourceProvider;
import jsfgenerator.ui.wizards.MVCGenerationWizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ViewAndControllerGenerationAction extends Action implements IObjectActionDelegate {

	protected IWorkbenchPart part;

	protected IFile selectedResource;

	public void setActivePart(IAction action, IWorkbenchPart part) {
		this.part = part;
	}

	public void run(IAction action) {

		if (selectedResource != null) {
			IJavaProject project = JavaCore.create(selectedResource.getProject());
			ProjectResourceProvider.getInstance().setJavaProject(project);
			List<EntityDescription> entityDescriptions = EntityClassParser.findEntities(selectedResource);

			WizardDialog dialog = new WizardDialog(part.getSite().getShell(), new MVCGenerationWizard(entityDescriptions));
			dialog.open();
		} else {
			ProjectResourceProvider.getInstance().setJavaProject(null);
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {

		if (selection instanceof StructuredSelection) {
			Object element = ((StructuredSelection) selection).getFirstElement();
			if (element instanceof IFile) {
				selectedResource = (IFile) element;
			} else {
				selectedResource = null;
			}

		}
	}
}
