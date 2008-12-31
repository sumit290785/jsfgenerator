package jsfgenerator.ui.providers;

import jsfgenerator.generation.common.INameConstants;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Label provider class to provide standard images and text format for elements of a project like files, folders, packages
 * 
 * @author zoltan verebes
 * 
 */
@SuppressWarnings("restriction")
public class ResourceLabelProvider extends LabelProvider {

	private static final Image IMG_FOLDER = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
	
	private static final Image IMG_PROJECT = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_PROJECT);

	private static final Image IMG_FILE = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);

	private static final Image IMG_PACKAGE = JavaPluginImages.get(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKAGE);

	private static final Image IMG_PACKAGE_EMPTY = JavaPluginImages.get(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_EMPTY_PACKAGE);

	private static final Image IMG_GEN = new Image(PlatformUI.getWorkbench().getDisplay(), ResourceLabelProvider.class
			.getResourceAsStream("/resource/images/applications-system.png"));

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof IFolder) {
			return IMG_FOLDER;
		}
		
		if (element instanceof IProject) {
			return IMG_PROJECT;
		}

		if (element instanceof IFile) {

			if (((IFile) element).getFileExtension().equals(INameConstants.VIEW_XML_EXTENSION)) {
				return IMG_GEN;
			}

			return IMG_FILE;
		}

		if (element instanceof IPackageFragment) {
			try {
				if (((IPackageFragment) element).getChildren().length == 0) {
					return IMG_PACKAGE_EMPTY;
				} else {
					return IMG_PACKAGE;
				}
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		} else if (element instanceof IPackageFragment) {
			if (((IPackageFragment) element).isDefaultPackage()) {
				return "{default package}";
			}

			return ((IPackageFragment) element).getElementName();
		}

		return super.getText(element);
	}

}
