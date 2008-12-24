package jsfgenerator.ui.wizards;

import org.eclipse.core.resources.IProject;
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
		monitor.beginTask("", 2);

		try {
			monitor.worked(1);
		} finally {
			monitor.done();
		}

	}

}
