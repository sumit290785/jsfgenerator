package jsfgenerator.ui.model;

import jsfgenerator.ui.astvisitors.EntityClassASTVisitor;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
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

	public TypeDeclaration findSingleClassTypeDeclaration(String fullyQualifiedClassName) {

		if (javaProject == null) {
			throw new NullPointerException("Java project is not registered!");
		}

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		try {
			ICompilationUnit unit =  javaProject.findType(fullyQualifiedClassName).getCompilationUnit();
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
}
