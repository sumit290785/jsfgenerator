package jsfgenerator.ui.wizards;

import java.io.IOException;

import jsfgenerator.ui.artifacthandlers.ArtifactEditHandler;
import jsfgenerator.ui.model.ProjectResourceProvider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
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

	public void execute(final IProject jsfProject, final IProjectFacetVersion facetVersion, Object config,
			IProgressMonitor monitor) throws CoreException {

		ProjectResourceProvider resources = ProjectResourceProvider.getInstance();

		resources.setJsfProject(jsfProject);

		JSFGenInstallConfig jsfConfig = (JSFGenInstallConfig) config;
		final IProject earProject = getReferencedEar(jsfProject);

		if (earProject == null) {
			throw new NullPointerException("Referenced EAR project not found");
		}

		resources.setEarProject(earProject);
		final IProject ejbProject = jsfConfig.getEjbProject();
		resources.setEjbProject(jsfConfig.getEjbProject());

		IProjectDescription desc = earProject.getDescription();
		IProject[] refs = desc.getReferencedProjects();
		IProject[] newRefs = new IProject[refs.length + 1];
		System.arraycopy(refs, 0, newRefs, 0, refs.length);
		newRefs[refs.length] = jsfConfig.getEjbProject();
		desc.setReferencedProjects(newRefs);
		earProject.setDescription(desc, monitor);

		monitor.beginTask("Create xml and xsd files", 20);

		try {

			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					ProjectResourceProvider resources = ProjectResourceProvider.getInstance();

					try {
						/*
						 * JSF Project
						 */
						{
							monitor.subTask("Create: /resources");
							IFolder resourceFolder = jsfProject.getFolder("resources");

							if (!resourceFolder.exists()) {
								resourceFolder.create(true, true, monitor);
							}
							monitor.worked(1);

							// jsf project / resource
							monitor.subTask("Copy: /resources/view.xml");
							IFile viewfile = resourceFolder.getFile("view.xml");
							viewfile.create(resources.getViewSkeletonInputStream(), IResource.FORCE, monitor);
							monitor.worked(1);

							monitor.subTask("Copy: /resources/annotation.xsd");
							IFile xsdfile = resourceFolder.getFile("annotation.xsd");
							xsdfile.create(resources.getViewSchemaInputStream(), IResource.FORCE, monitor);
							monitor.worked(1);

							// jsf project / WebContent
							IFolder webContentFolder = jsfProject.getFolder("WebContent");

							// jsf project / WebContent / layout
							monitor.subTask("Create: /WebContent/layout");
							IFolder layoutFolder = webContentFolder.getFolder("layout");
							if (!layoutFolder.exists()) {
								layoutFolder.create(true, true, monitor);
							}
							monitor.worked(1);

							monitor.subTask("Copy: /WebContent/layout/template.xhtml");
							IFile templateFile = layoutFolder.getFile("template.xhtml");
							templateFile.create(resources.getViewTemplateInputStream(), IResource.FORCE, monitor);
							monitor.worked(1);

							// jsf project / WebContent / WEB-INF
							IFolder webInfFolder = webContentFolder.getFolder("WEB-INF");

							monitor.subTask("Copy: /WebContent/WEB-INF/web.xml");
							IFile webAppFile = webInfFolder.getFile("web.xml");
							if (webAppFile.exists()) {
								webAppFile.delete(true, monitor);
							}
							webAppFile.create(resources.getWebAppInputStream(), IResource.FORCE, monitor);
							monitor.worked(1);

							monitor.subTask("Copy: /WebContent/WEB-INF/faces-config.xml");
							IFile facesConfigFile = webInfFolder.getFile("faces-config.xml");
							if (facesConfigFile.exists()) {
								facesConfigFile.delete(true, monitor);
							}
							facesConfigFile.create(resources.getFacesConfigInputStream(), IResource.FORCE, monitor);
							monitor.worked(1);

							// jsf project / WebContent / WEB-INF / taglibs
							monitor.subTask("Create: /WebContent/taglibs");
							IFolder taglibsFolder = webInfFolder.getFolder("taglibs");
							if (!taglibsFolder.exists()) {
								taglibsFolder.create(true, true, monitor);
							}
							monitor.worked(1);

							monitor.subTask("Copy: /WebContent/taglibs/jsfgen.taglib.xml");
							IFile taglibFile = taglibsFolder.getFile("jsfgen.taglib.xml");
							taglibFile.create(resources.getTaglibInputStream(), IResource.FORCE, monitor);
							monitor.worked(1);

							// jsf project / WebContent / WEB-INF / lib
							IFolder libFolder = webInfFolder.getFolder("lib");

							monitor.subTask("Copy: /WebContent/lib/jsfgen-web.jar");
							IFile jsfJarFile = libFolder.getFile("jsfgen-web.jar");
							jsfJarFile.create(resources.getJSFGenWebJar(), IResource.FORCE, monitor);
							monitor.worked(1);

							monitor.subTask("Copy: /WebContent/lib/jsf-facelets.jar");
							IFile faceletsJarFile = libFolder.getFile("jsf-facelets.jar");
							faceletsJarFile.create(resources.getFacletsJar(), IResource.FORCE, monitor);
							monitor.worked(1);
						}
						/*
						 * EAR project
						 */
						{
							// /EarContent
							IFolder earContentFolder = earProject.getFolder("EarContent");

							monitor.subTask("Copy: /EarContent/jsfgen-ejb.jar");
							IFile ejbJarFile = earContentFolder.getFile("jsfgen-ejb.jar");
							ejbJarFile.create(resources.getJSFGenEjbJar(), IResource.FORCE, monitor);
							monitor.worked(1);

							// /EarContent/META-INF
							monitor.subTask("Create: /EarContent/META-INF");
							IFolder metaInfFolder = earContentFolder.getFolder("META-INF");

							if (!metaInfFolder.exists()) {
								metaInfFolder.create(true, true, monitor);
							}

							IFile appXmlFile = metaInfFolder.getFile("application.xml");
							appXmlFile.create(resources.getApplicationXmlInputStream(), IResource.FORCE, monitor);
							monitor.worked(1);
						}

						/*
						 * EJB project
						 */
						{
							// /src/META-INF
							monitor.subTask("Create: /src/META-INF");
							IFolder srcFolder = ejbProject.getFolder("src");
							IFolder metaInfFolder = srcFolder.getFolder("META-INF");

							if (!metaInfFolder.exists()) {
								metaInfFolder.create(true, true, monitor);
							}
							IFile ejbJarXmlFile = metaInfFolder.getFile("ejb-jar.xml");
							ejbJarXmlFile.create(resources.getEjbJarXmlInputStream(), IResource.FORCE, monitor);
							monitor.worked(1);

						}

						/*
						 * modify the copied config files
						 */
						ArtifactEditHandler handler = ArtifactEditHandler.getInstance();
						handler.setDispayName(jsfProject.getName());
						handler.configEarApplication(jsfProject.getName(), resources.getEjbProject().getName());

						resources.addModuleToProject(resources.getEarProject(), resources.getEjbProject());
						resources.addModuleToProject(resources.getJsfProject(), resources.getEjbProject());

					} catch (IOException e) {
						throw new RuntimeException(e.getMessage(), e);
					}
				}
			}, monitor);

		} finally {
			monitor.done();
		}
	}

	private IProject getReferencedEar(IProject jsfProject) {
		for (IProject ref : jsfProject.getReferencingProjects()) {
			if (ProjectResourceProvider.isEarProject(ref)) {
				return ref;
			}
		}

		return null;
	}
}
