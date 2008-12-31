package jsfgenerator.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;

public final class JSFGenInstallConfig {

	private IProject ejbProject;

	public static final class Factory implements IActionConfigFactory {
		public Object create() {
			return new JSFGenInstallConfig();
		}
	}

	public void setEjbProject(IProject ejbProject) {
		this.ejbProject = ejbProject;
	}

	public IProject getEjbProject() {
		return ejbProject;
	}

}
