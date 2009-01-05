package jsfgenerator.ui.actions;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.generation.common.GenerationException;
import jsfgenerator.ui.validation.ValidationResultDialog;
import jsfgenerator.ui.validation.ViewTemplateValidator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ValidationAction extends Action implements IObjectActionDelegate {

	private IWorkbenchPart part;

	private StructuredSelection selection;

	public void setActivePart(IAction action, IWorkbenchPart part) {
		this.part = part;
	}

	public void run(IAction action) {

		if (selection == null || !(selection.getFirstElement() instanceof IFile)) {
			return;
		}

		ViewTemplateValidator validator = new ViewTemplateValidator((IFile) selection.getFirstElement());
		validator.validate();

		ValidationResultDialog dialog = new ValidationResultDialog(part.getSite().getShell(), validator.getMessages());
		dialog.open();

	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof StructuredSelection) {
			this.selection = (StructuredSelection) selection;
		} else {
			this.selection = null;
		}
	}

}
