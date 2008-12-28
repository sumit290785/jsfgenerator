package jsfgenerator.ui.wizards;

import java.io.IOException;

import jsfgenerator.ui.artifacthandlers.ArtifactEditHandler;
import jsfgenerator.ui.model.ProjectResourceProvider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
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

		ProjectResourceProvider.getInstance().setJavaProject(JavaCore.create(project));

		monitor.beginTask("Create xml and xsd files", 5);

		try {

			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IFolder folder = project.getFolder("resources");

					if (!folder.exists()) {
						folder.create(true, true, monitor);
					}

					try {
						ProjectResourceProvider resources = ProjectResourceProvider.getInstance();
						
						// view skeleton
						IFile viewfile = folder.getFile("view.xml");
						viewfile.create(resources.getViewSkeletonInputStream(), IResource.FORCE, monitor);
						monitor.worked(1);

						monitor.worked(1);

						// XML Schema file
						IFile xsdfile = folder.getFile("annotation.xsd");
						xsdfile.create(resources.getViewSchemaInputStream(), IResource.FORCE, monitor);

						// web folder
						IFolder webContentFolder = project.getFolder("WebContent");
						IFolder layoutFolder = webContentFolder.getFolder("layout");
						if (!layoutFolder.exists()) {
							layoutFolder.create(true, true, monitor);
						}

						// template file
						IFile templateFile = layoutFolder.getFile("template.xhtml");
						templateFile.create(resources.getViewTemplateInputStream(), IResource.FORCE, monitor);

						IFolder webInfFolder = webContentFolder.getFolder("WEB-INF");

						// lib folder
						IFolder libFolder = webInfFolder.getFolder("lib");

						// jars
						IFile jarFile = libFolder.getFile("jsfgen.jar");
						jarFile.create(resources.getJSFGenJar(), IResource.FORCE, monitor);

						IFile faceletsJarFile = libFolder.getFile("jsf-facelets.jar");
						faceletsJarFile.create(resources.getFacletsJar(), IResource.FORCE, monitor);

						// faclet config and web deployment descriptor files
						IFile webAppFile = webInfFolder.getFile("web.xml");
						if (webAppFile.exists()) {
							webAppFile.delete(true, monitor);
						}
						webAppFile.create(resources.getWebAppInputStream(), IResource.FORCE, monitor);

						ArtifactEditHandler.getInstance().setDispayName(project.getName());

						IFile facesConfigFile = webInfFolder.getFile("faces-config.xml");
						if (facesConfigFile.exists()) {
							facesConfigFile.delete(true, monitor);
						}
						facesConfigFile.create(resources.getFacesConfigInputStream(), IResource.FORCE, monitor);
						monitor.worked(1);
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
