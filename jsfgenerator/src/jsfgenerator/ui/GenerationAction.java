package jsfgenerator.ui;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class GenerationAction extends Action implements IObjectActionDelegate {

	private IWorkbenchPart part;

	public void setActivePart(IAction action, IWorkbenchPart part) {
		this.part = part;
	}

	public void run(IAction action) {
		MessageDialog.openInformation(this.part.getSite().getShell(), "Readme Example", "Popup Menu Action executed");
	}

	@SuppressWarnings("restriction")
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			Object element = ((StructuredSelection) selection).getFirstElement();

			File selectedFile = (File) element;
			IFile file = selectedFile.getParent().getFile(selectedFile.getLocation());
			
			System.out.println(file.getName());
		}
	}

}
