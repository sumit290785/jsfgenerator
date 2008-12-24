package jsfgenerator.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * Install delegate for dynamic web project facet. Configured in plugin.xml. It creates a skeleton of the generation project
 * 
 * @author zoltan verebes
 * 
 */
public final class MVCFacetInstallDelegate implements IDelegate {

	public void execute(IProject project, IProjectFacetVersion facetVersion, Object config, IProgressMonitor monitor)
			throws CoreException {
		monitor.beginTask("Create xml and xsd files", 3);

		try {
			IFolder folder = project.getFolder("resources");

			if (!folder.exists()) {
				folder.create(true, true, monitor);
			}

			monitor.worked(1);
			IFile viewfile = folder.getFile("tagtree.view");
			viewfile.create(getClass().getResourceAsStream("/resource/view.xml"), IResource.FORCE, null);
			monitor.worked(2);

			IFile xsdfile = folder.getFile("view.xsd");
			xsdfile.create(getClass().getResourceAsStream("/resource/view.xsd"), IResource.FORCE, null);
		} finally {
			monitor.done();
		}
	}

}
