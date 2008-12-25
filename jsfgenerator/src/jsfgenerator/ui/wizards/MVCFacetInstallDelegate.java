package jsfgenerator.ui.wizards;

import java.io.IOException;

import jsfgenerator.ui.model.ProjectResourceProvider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
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

	public void execute(final IProject project, final IProjectFacetVersion facetVersion, Object config, IProgressMonitor monitor)
			throws CoreException {
		monitor.beginTask("Create xml and xsd files", 3);

		try {

			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IFolder folder = project.getFolder("resources");

					if (!folder.exists()) {
						folder.create(true, true, monitor);
					}

					try {
						ProjectResourceProvider resources = ProjectResourceProvider.getInstance();
						monitor.worked(1);
						IFile viewfile = folder.getFile("tagtree.view");

						monitor.worked(2);

						// XML Scheme file
						IFile xsdfile = folder.getFile("view.xsd");
						xsdfile.create(resources.getViewSchemaInputStream(), IResource.FORCE, monitor);

						// web folder
						IFolder webFolder = project.getFolder("WebContent/WEB-INF");
						IFolder layoutFolder = webFolder.getFolder("layout");
						if (!layoutFolder.exists()) {
							layoutFolder.create(true, true, monitor);
						}

						// template file
						IFile templateFile = layoutFolder.getFile("template.xhtml");
						templateFile.create(resources.getViewTemplateInputStream(), IResource.FORCE, monitor);

						// lib folder
						IFolder libFolder = webFolder.getFolder("lib");

						// jar
						IFile jarFile = libFolder.getFile("jsfgen.jar");
						jarFile.create(resources.getJSFGenJar(), IResource.FORCE, monitor);

						// view skeleton
						viewfile.create(resources.getViewSkeletonInputStream(), IResource.FORCE, monitor);
					} catch (IOException e) {
						throw new RuntimeException(e.getMessage(), e);
					}
				}
			}, monitor);

		} finally {
			monitor.done();
		}
	}
}
