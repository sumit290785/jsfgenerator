package jsfgenerator.entitymodel.utilities;

import java.util.List;

import jsfgenerator.ui.wizards.EntityWizardInput;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * Utility class to parse AST elements and find the entities in the java model!
 * 
 * @author zoltan verebes
 * 
 */
public class EntityParser {

	/**
	 * parses a compilation unit! It can contain multiple classes, so it returns
	 * with a list of classes!
	 * 
	 * @param resource
	 * @return classes declared in the compilatition unit
	 */
	public static List<EntityWizardInput> findEntities(IFile resource) {

		if (resource == null) {
			throw new IllegalArgumentException("Resource parameter cannot be null!");
		}

		// AST representation of the resource file
		ICompilationUnit unit = (ICompilationUnit) JavaCore.create(resource);

		// JLS3 - knows all of the Java 5 elements
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);

		return findEntities(parser);
	}

	/**
	 * TODO
	 * 
	 * @param project
	 * @return
	 */
	public static List<EntityWizardInput> findEntities(IProject project) {
		return null;
	}

	/**
	 * Builds a name for the getter of the field
	 * 
	 * @param fragment
	 * @return getter method name of the field
	 */
	public static String getGetterName(VariableDeclarationFragment fragment) {
		if (fragment == null) {
			throw new IllegalArgumentException("Field parameter cannot be null!");
		}

		if (fragment.getName().getFullyQualifiedName() == null || fragment.getName().getFullyQualifiedName().equals("")) {
			throw new IllegalArgumentException("Field parameter is incorrect!");
		}

		return "get" + capitalFieldName(fragment.getName().getFullyQualifiedName());
	}

	/**
	 * Builds a name for the setter of the field
	 * 
	 * @param fragment
	 * @return getter method name of the field
	 */
	public static String getSetterName(VariableDeclarationFragment fragment) {
		if (fragment == null) {
			throw new IllegalArgumentException("Field parameter cannot be null!");
		}

		if (fragment.getName().getFullyQualifiedName() == null || fragment.getName().getFullyQualifiedName().equals("")) {
			throw new IllegalArgumentException("Field parameter is incorrect!");
		}

		return "set" + capitalFieldName(fragment.getName().getFullyQualifiedName());
	}

	/**
	 * method is the getter of fieldName
	 * 
	 * A method is getter of a field called xxx if it is called getXxx(), return
	 * type is the same as the field type and has no formal parameters!
	 * 
	 * @param method
	 * @param field
	 * @return
	 */
	public static boolean isGetterOf(MethodDeclaration method, VariableDeclarationFragment fragment, Type type) {
		// check the name
		if (!method.getName().getFullyQualifiedName().equals(getGetterName(fragment))) {
			return false;
		}

		// check the return type
		if (!method.getReturnType2().toString().equals(type.toString())) {
			return false;
		}

		// has no formal parameter
		if (method.parameters().size() != 0) {
			return false;
		}

		return true;
	}

	/**
	 * method is the setter of fieldName
	 * 
	 * A method is setter of a field called xxx if it is called setXxx(), return
	 * type is void and has exactly one parameter which type is the same as the
	 * field type
	 * 
	 * @param method
	 * @param field
	 * @return
	 */
	public static boolean isSetterOf(MethodDeclaration method, VariableDeclarationFragment fragment, Type type) {
		// check the name
		if (!method.getName().getFullyQualifiedName().equals(getSetterName(fragment))) {
			return false;
		}

		// check the return type is void
		if (!method.getReturnType2().isPrimitiveType()
				|| !((PrimitiveType) method.getReturnType2()).getPrimitiveTypeCode().equals(PrimitiveType.VOID)) {
			return false;
		}

		// has one formal parameter and its type is the same as the type
		// parameter
		if (method.parameters().size() != 1 && !method.parameters().get(0).equals(type)) {
			return false;
		}

		return true;
	}

	protected static List<EntityWizardInput> findEntities(ASTParser parser) {
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		// use ASTVisitor to find the entity classes in the tree of java model
		EntityVisitor visitor = new EntityVisitor();
		cu.accept(visitor);

		return visitor.getEntityWizardInputs();
	}

	protected static String capitalFieldName(String name) {
		if (name == null || name.length() == 0) {
			return name;
		}

		StringBuffer buffer = new StringBuffer();
		buffer.append(Character.toUpperCase(name.charAt(0)));

		if (name.length() != 1) {
			buffer.append(name.substring(1));
		}

		return buffer.toString();
	}

}
