package jsfgenerator.ui.artifacthandlers;

import java.util.Iterator;

import jsfgenerator.ui.model.ProjectResourceProvider;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.j2ee.application.Application;
import org.eclipse.jst.j2ee.application.EjbModule;
import org.eclipse.jst.j2ee.application.WebModule;
import org.eclipse.jst.j2ee.componentcore.util.EARArtifactEdit;
import org.eclipse.jst.j2ee.web.componentcore.util.WebArtifactEdit;
import org.eclipse.jst.j2ee.webapplication.ContextParam;
import org.eclipse.jst.j2ee.webapplication.Servlet;
import org.eclipse.jst.j2ee.webapplication.ServletMapping;
import org.eclipse.jst.j2ee.webapplication.WebApp;
import org.eclipse.jst.j2ee.webapplication.WebapplicationFactory;
import org.eclipse.jst.j2ee.webapplication.WebapplicationPackage;
import org.eclipse.jst.jsf.facesconfig.emf.FacesConfigFactory;
import org.eclipse.jst.jsf.facesconfig.emf.FacesConfigPackage;
import org.eclipse.jst.jsf.facesconfig.emf.ManagedBeanClassType;
import org.eclipse.jst.jsf.facesconfig.emf.ManagedBeanNameType;
import org.eclipse.jst.jsf.facesconfig.emf.ManagedBeanScopeType;
import org.eclipse.jst.jsf.facesconfig.emf.ManagedBeanType;
import org.eclipse.jst.jsf.facesconfig.util.FacesConfigArtifactEdit;
import org.eclipse.wst.common.componentcore.ArtifactEdit;

/**
 * Singleton class to modify faces-config.xml, web.xml, application.xml via built in artifact edits
 * 
 * @author zoltan verebes
 * 
 */
public final class ArtifactEditHandler {

	private static ArtifactEditHandler instance;

	private IProgressMonitor monitor;

	private ArtifactEditHandler() {

	}

	public static ArtifactEditHandler getInstance() {
		if (instance == null) {
			instance = new ArtifactEditHandler();
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	public void addManagedBeanToFacesConfig(String viewId, String className) {

		FacesConfigArtifactEdit edit = getFacesConfigArtifectEdit();

		Iterator it = edit.getFacesConfig().getManagedBean().iterator();
		while (it.hasNext()) {
			ManagedBeanType mbt = (ManagedBeanType) it.next();
			if (mbt.getManagedBeanName().getTextContent().equals(viewId)) {
				it.remove();
				break;
			}
		}

		FacesConfigPackage facesConfigPackage = FacesConfigPackage.eINSTANCE;
		FacesConfigFactory facesConfigFactory = facesConfigPackage.getFacesConfigFactory();
		ManagedBeanType managedBT = facesConfigFactory.createManagedBeanType();

		ManagedBeanNameType managedBeanNameType = facesConfigFactory.createManagedBeanNameType();
		managedBeanNameType.setTextContent(viewId);
		managedBT.setManagedBeanName(managedBeanNameType);

		ManagedBeanClassType managedBeanClassType = facesConfigFactory.createManagedBeanClassType();
		managedBeanClassType.setTextContent(className);
		managedBT.setManagedBeanClass(managedBeanClassType);

		ManagedBeanScopeType managedBeanScopeType = facesConfigFactory.createManagedBeanScopeType();
		managedBeanScopeType.setTextContent("request");
		managedBT.setManagedBeanScope(managedBeanScopeType);
		edit.getFacesConfig().getManagedBean().add(managedBT);

		saveEdit(edit);
	}

	public void setDispayName(String projectName) {
		WebArtifactEdit webEdit = getWebArtifactEdit();
		WebApp webApp = webEdit.getWebApp();
		webApp.setDisplayName(projectName);
		saveEdit(webEdit);
	}
	
	public void configEarApplication(String webProjectName, String ejbProjectName) {
		EARArtifactEdit earEdit = getEarArtifactEdit();
		Application app = earEdit.getApplication();
		app.setDisplayName(webProjectName + "-ear");
		
		for (Object module : app.getModules()) {
			if (module instanceof WebModule) {
				WebModule webModule = (WebModule) module;
				webModule.setContextRoot("/" + webProjectName);
				webModule.setUri(webProjectName + ".war");
			}
			
			if (module instanceof EjbModule) {
				EjbModule ejbModule = (EjbModule) module;
				ejbModule.setUri(ejbProjectName + ".jar");
			}
		}
		
		saveEdit(earEdit);
	}

	@SuppressWarnings("unchecked")
	public void addFacletApplicationToWebDeploymentDescriptor() {
		WebArtifactEdit edit = getWebArtifactEdit();

		WebApp app = edit.getWebApp();
		WebapplicationFactory factory = WebapplicationPackage.eINSTANCE.getWebapplicationFactory();

		// remove faces servlet and it mappings
		Servlet servlet = app.getServletNamed("Faces Servlet");
		if (servlet != null) {
			ServletMapping mapping = app.getServletMapping(servlet);
			mapping.setUrlPattern("*.jsf");
		} else {
			servlet = factory.createServlet();
			servlet.setServletName("Faces Servlet");
			servlet.setLoadOnStartup(1);
			servlet.getServletClass().setName("javax.faces.webapp.FacesServlet");

			ServletMapping mapping = factory.createServletMapping();
			mapping.setName("Faces servlet");
			mapping.setUrlPattern("*.jsf");
			app.getServletMappings().add(mapping);
		}

		Iterator it = app.getContextParams().iterator();
		ContextParam param = null;
		while (it.hasNext()) {
			param = (ContextParam) it.next();
			if (param.getParamName().equals("javax.faces.DEFAULT_SUFFIX")) {
				param.setParamValue(".xhtml");
				break;
			}
			param = null;
		}

		if (param == null) {
			param = factory.createContextParam();
			param.setParamName("javax.faces.DEFAULT_SUFFIX");
			param.setParamValue(".xhtml");
			app.getContextParams().add(param);
		}

		saveEdit(edit);
	}

	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	public IProgressMonitor getMonitor() {
		return monitor;
	}

	protected FacesConfigArtifactEdit getFacesConfigArtifectEdit() {
		FacesConfigArtifactEdit edit = new FacesConfigArtifactEdit(ProjectResourceProvider.getInstance().getJsfJavaProject()
				.getProject(), false);

		if (edit == null) {
			throw new IllegalArgumentException("faces-config.xml not found in the selected project");
		}

		edit.setFilename("/WebContent/WEB-INF/faces-config.xml");

		if (edit.getFacesConfig() == null) {
			throw new IllegalArgumentException("faces-config.xml not found in the selected project");
		}

		return edit;
	}

	protected WebArtifactEdit getWebArtifactEdit() {
		WebArtifactEdit edit = new WebArtifactEdit(ProjectResourceProvider.getInstance().getJsfProject(), false);

		if (edit == null) {
			throw new IllegalArgumentException("web.xml not found in the selected project");
		}

		if (edit.getWebApp() == null) {
			throw new IllegalArgumentException("web.xml not found in the web project");
		}

		return edit;
	}

	protected EARArtifactEdit getEarArtifactEdit() {
		EARArtifactEdit edit = new EARArtifactEdit(ProjectResourceProvider.getInstance().getEarProject(), false);
		if (edit == null) {
			throw new IllegalArgumentException("application.xml not found in the ear project");
		}

		if (edit.getApplication() == null) {
			throw new IllegalArgumentException("application.xml not found in the ear project");
		}

		return edit;
	}

	protected void saveEdit(ArtifactEdit edit) {
		edit.saveIfNecessary(getMonitor());
		edit.dispose();
	}
}
