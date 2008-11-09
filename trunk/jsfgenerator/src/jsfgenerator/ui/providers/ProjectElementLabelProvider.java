package jsfgenerator.ui.providers;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Label provider class to provide standard images and text format for elements
 * of a project like files, folders, packages
 * 
 * @author zoltan verebes
 * 
 */
public class ProjectElementLabelProvider extends LabelProvider {

	private static final Image IMG_FOLDER = PlatformUI.getWorkbench().getSharedImages().getImage(
			ISharedImages.IMG_OBJ_FOLDER);

	private static final Image IMG_FILE = PlatformUI.getWorkbench().getSharedImages().getImage(
			ISharedImages.IMG_OBJ_FILE);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
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
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof IContainer || element instanceof IFile) {
			return ((IResource) element).getName();
		}

		return super.getText(element);
	}

}
