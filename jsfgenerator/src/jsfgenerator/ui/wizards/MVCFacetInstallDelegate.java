package jsfgenerator.ui.wizards;

import jsfgenerator.ui.artifacthandlers.ArtifactEditHandler;
import jsfgenerator.ui.utilities.ProjectResourceProvider;
import jsfgenerator.ui.utilities.Resources;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
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
		resources.setEjbProject(jsfConfig.getEjbProject());

		IProjectDescription desc = earProject.getDescription();
		IProject[] refs = desc.getReferencedProjects();
		IProject[] newRefs = new IProject[refs.length + 1];
		System.arraycopy(refs, 0, newRefs, 0, refs.length);
		newRefs[refs.length] = jsfConfig.getEjbProject();
		desc.setReferencedProjects(newRefs);
		earProject.setDescription(desc, monitor);

		ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {

				Resources.getInstance().copyAll(monitor);

				/*
				 * modify the copied config files
				 */

				ProjectResourceProvider resources = ProjectResourceProvider.getInstance();
				ArtifactEditHandler handler = ArtifactEditHandler.getInstance();
				handler.setDispayName(jsfProject.getName());
				handler.configEarApplication(jsfProject.getName(), resources.getEjbProject().getName());

				resources.addModuleToProject(resources.getEarProject(), resources.getEjbProject());
				resources.addModuleToProject(resources.getJsfProject(), resources.getEjbProject());
			}
		}, monitor);

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
