package jsfgenerator.ui.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import jsfgenerator.Activator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

public class Resources {

	private static final String RESOURCE_FOLDER = "/resource/";

	private static Resources instance;

	private IProgressMonitor monitor;

	private Resources() {

	}

	public static Resources getInstance() {
		if (instance == null) {
			instance = new Resources();
		}
		return instance;
	}

	public void copyAll(IProgressMonitor monitor) {
		this.monitor = monitor;

		IProject jsfProject = ProjectResourceProvider.getInstance().getJsfProject();
		IProject ejbProject = ProjectResourceProvider.getInstance().getEjbProject();
		IProject earProject = ProjectResourceProvider.getInstance().getEarProject();

		monitor.beginTask("Copy resource files into the new projects", 11);

		try {
			copy(jsfProject, "/resources/view.xml");
			copy(jsfProject, "/resources/annotations.xsd");
			copy(jsfProject, "/WebContent/layout/template.xhtml");
			copy(jsfProject, "/WebContent/WEB-INF/web.xml");
			copy(jsfProject, "/WebContent/WEB-INF/faces-config.xml");
			copy(jsfProject, "/WebContent/taglibs/jsfgen.taglib.xml");
			copy(jsfProject, "/WebContent/lib/jsfgen-web.jar");
			copy(jsfProject, "/WebContent/lib/jsf-facelets.jar");

			copy(earProject, "/EarContent/jsfgen-ejb.jar");
			copy(earProject, "/EarContent/META-INF/application.xml");

			copy(ejbProject, "/src/META-INF/ejb-jar.xml");
		} catch (CoreException e) {
			throw new RuntimeException("Could not copy the resources", e);
		} catch (IOException e) {
			throw new RuntimeException("Could not copy the resources", e);
		} finally {
			monitor.done();
		}

	}

	private void copy(IProject project, String path) throws CoreException, IOException {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		String[] tokens = path.split("[/]");
		monitor.subTask("Copy: /" + project.getName() + "/" + path);
		if (tokens.length == 0) {
			return;
		}

		String fileName = tokens[tokens.length - 1];
		InputStream stream = getStream(fileName);

		if (stream == null) {
			throw new IllegalArgumentException("Source not found");
		}

		IFolder folder = project.getFolder(tokens[0]);
		if (!folder.exists()) {
			folder.create(true, true, monitor);
		}
		
		for (int i = 1; i < tokens.length - 1; i++) {
			String folderName = tokens[i];
			folder = folder.getFolder(folderName);
			if (!folder.exists()) {
				folder.create(true, true, monitor);
			}
		}

		IFile file = folder.getFile(fileName);
		if (file.exists()) {
			file.delete(true, monitor);
		}
		file.create(stream, IResource.FORCE, monitor);
		monitor.worked(1);
	}

	private InputStream getStream(String fileName) throws IOException {
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(RESOURCE_FOLDER + fileName), null);
		return url.openStream();
	}

}
