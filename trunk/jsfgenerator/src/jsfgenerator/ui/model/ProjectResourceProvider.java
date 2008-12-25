package jsfgenerator.ui.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jsfgenerator.Activator;
import jsfgenerator.generation.common.GenerationException;
import jsfgenerator.ui.astvisitors.EntityClassASTVisitor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * Singleton class to access resources from the project(s) of the generation. Project is registered at the beginning of any generation
 * action.
 * 
 * @author zoltan verebes
 * 
 */
public class ProjectResourceProvider {

	protected static ProjectResourceProvider instance;

	private IJavaProject javaProject;

	public static ProjectResourceProvider getInstance() {
		if (instance == null) {
			instance = new ProjectResourceProvider();
		}

		return instance;
	}

	public void setJavaProject(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public IProject getProject() {
		if (javaProject == null) {
			throw new NullPointerException("Java project is not found!");
		}

		return javaProject.getProject();
	}

	public Collection<IJavaElement> getProjectPackageFragments() {
		if (javaProject == null) {
			throw new NullPointerException("Java project is not found!");
		}

		Set<IJavaElement> fragments = new HashSet<IJavaElement>();
		try {
			for (IClasspathEntry entry : javaProject.getRawClasspath()) {
				for (IPackageFragmentRoot root : javaProject.findPackageFragmentRoots(entry)) {

					if (!root.isArchive() && !root.isReadOnly()) {
						fragments.addAll(Arrays.asList(root.getChildren()));
					}

				}
			}
			return fragments;
		} catch (JavaModelException e1) {
			throw new NullPointerException("Could not load packages");
		}
	}

	public TypeDeclaration findSingleClassTypeDeclaration(String fullyQualifiedClassName) {

		if (javaProject == null) {
			throw new NullPointerException("Java project is not registered!");
		}

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		try {
			ICompilationUnit unit = javaProject.findType(fullyQualifiedClassName).getCompilationUnit();

			if (unit == null) {
				throw new GenerationException("Entity source not found! Reason: " + fullyQualifiedClassName
						+ " is not an entity!");
			}

			parser.setSource(unit);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			EntityClassASTVisitor visitor = new EntityClassASTVisitor();
			visitor.setClassName(fullyQualifiedClassName);
			cu.accept(visitor);

			return visitor.getSingleClassTypeDeclaration();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public InputStream getViewSkeletonInputStream() throws IOException {
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path("/resource/view.xml"), null);
		return url.openStream();
	}

	public InputStream getViewSchemaInputStream() throws IOException {
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path("/resource/view.xsd"), null);
		return url.openStream();
	}

	public InputStream getJSFGenJar() throws IOException {
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path("/resource/jsfgen.jar"), null);
		return url.openStream();
	}

	public InputStream getFacletsJar() throws IOException {
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path("/resource/jsf-facelets.jar"), null);
		return url.openStream();
	}

	public InputStream getViewTemplateInputStream() throws IOException {
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path("/resource/template.xhtml"), null);
		return url.openStream();
	}

	public InputStream getWebAppInputStream() throws IOException {
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path("/resource/web.xml"), null);
		return url.openStream();
	}

	public InputStream getFacesConfigInputStream() throws IOException {
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path("/resource/faces-config.xml"), null);
		return url.openStream();
	}

}
