package jsfgenerator.ui.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jsfgenerator.generation.common.GenerationException;
import jsfgenerator.ui.astvisitors.EntityClassASTVisitor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * Singleton class to access resources from the project(s) of the generation. Project is registered at the beginning of any generation
 * action.
 * 
 * @author zoltan verebes
 * 
 */
public class ProjectResourceProvider {

	protected static ProjectResourceProvider instance;

	private IProject jsfProject;

	private IProject earProject;

	private IProject ejbProject;

	public static ProjectResourceProvider getInstance() {
		if (instance == null) {
			instance = new ProjectResourceProvider();
		}

		return instance;
	}

	private IJavaProject getJavaProject(IProject project) {
		if (project != null) {
			return JavaCore.create(project);
		}

		return null;
	}

	public IJavaProject getJsfJavaProject() {
		return getJavaProject(jsfProject);
	}

	public IJavaProject getEjbJavaProject() {
		return getJavaProject(ejbProject);
	}

	private Collection<IJavaElement> getProjectPackageFragments(IProject project) {
		IJavaProject javaProject = getJavaProject(project);
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

	public Collection<IJavaElement> getJsfProjectPackageFragments() {
		return getProjectPackageFragments(jsfProject);
	}

	public Collection<IJavaElement> getEjbProjectPackageFragments() {
		return getProjectPackageFragments(ejbProject);
	}

	public TypeDeclaration findSingleClassTypeDeclarationInEjbProject(String fullyQualifiedClassName) {

		IJavaProject javaProject = getJsfJavaProject();
		if (javaProject == null) {
			throw new NullPointerException("EJB Java project is not registered!");
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
			throw new RuntimeException("Error in class type search!", e);
		}
	}

	public void findProjectsByEjbProject(IProject ejbProject) {
		this.ejbProject = ejbProject;
		for (IProject refProject : ejbProject.getReferencingProjects()) {
			if (isEarProject(refProject)) {
				this.earProject = refProject;
			}
		}

		if (this.earProject == null) {
			throw new NullPointerException("EAR project not found");
		}

		try {
			for (IProject refProject : earProject.getReferencedProjects()) {
				if (isJsfProject(refProject)) {
					this.jsfProject = refProject;
				}
			}
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}

		if (this.jsfProject == null) {
			throw new NullPointerException("JSF project not found");
		}

	}

	public void setEjbProject(IProject ejbProject) {
		this.ejbProject = ejbProject;
	}

	public IProject getEjbProject() {
		return ejbProject;
	}

	public void setEarProject(IProject earProject) {
		this.earProject = earProject;
	}

	public IProject getEarProject() {
		return earProject;
	}

	public void setJsfProject(IProject jsfProject) {
		this.jsfProject = jsfProject;
	}

	public IProject getJsfProject() {
		return jsfProject;
	}

	public static boolean isEarProject(IProject project) {
		for (IProjectFacet facet : getProjectFacets(project)) {
			if (facet.getId().equals("jst.ear")) {
				return true;
			}
		}

		return false;
	}

	public static boolean isEjbProject(IProject project) {
		for (IProjectFacet facet : getProjectFacets(project)) {
			if (facet.getId().equals("jpt.jpa") || facet.getId().equals("jst.ejb")) {
				return true;
			}
		}

		return false;
	}

	public static boolean isJsfProject(IProject project) {
		for (IProjectFacet facet : getProjectFacets(project)) {
			if (facet.getId().equals("jst.web")) {
				return true;
			}
		}

		return false;
	}

	private static Set<IProjectFacet> getProjectFacets(IProject project) {
		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			if (facetedProject == null) {
				return Collections.emptySet();
			}
			return facetedProject.getFixedProjectFacets();
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	public void addModuleToProject(IProject project, IProject module) {
		IVirtualComponent component = ComponentCore.createComponent(project);
		IVirtualReference ref = component.getReference(module.getName());

		if (ref == null) {
			ref = ComponentCore.createReference(component, ComponentCore.createComponent(module));
			component.addReferences(new IVirtualReference[] { ref });
		}
	}
	

	public static IProject[] getReferencingEARProjects(IProject project) {
		if (project != null && isEarProject(project)) {
			return new IProject[] { project };
		}

		List<IProject> result = new ArrayList<IProject>();
		IVirtualComponent component = ComponentCore.createComponent(project);
		if (component != null) {
			IVirtualComponent[] refComponents = component.getReferencingComponents();
			for (int i = 0; i < refComponents.length; i++) {
				if (isEarProject(refComponents[i].getProject()))
					result.add(refComponents[i].getProject());
			}
		}
		return (IProject[]) result.toArray(new IProject[result.size()]);
	}

}
