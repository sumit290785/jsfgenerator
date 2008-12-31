package jsfgenerator.ui.providers;

import java.util.Collection;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ResourceSelectionContentProvider implements ITreeContentProvider {
	private static final Object[] EMPTY = new Object[0];

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IWorkspaceRoot) {
			IWorkspaceRoot root = (IWorkspaceRoot) parentElement;
			return root.getProjects();
		}

		if (parentElement instanceof Collection) {
			return ((Collection<?>) parentElement).toArray();
		}

		if (parentElement instanceof Object[]) {
			return (Object[]) parentElement;
		}

		if (parentElement instanceof IContainer) {
			IContainer container = (IContainer) parentElement;
			IResource[] members = null;
			try {
				members = container.members(IContainer.FOLDER | IContainer.FILE | IContainer.PROJECT);
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
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang .Object)
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
