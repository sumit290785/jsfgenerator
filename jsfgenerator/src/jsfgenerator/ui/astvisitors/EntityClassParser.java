package jsfgenerator.ui.astvisitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jsfgenerator.generation.common.utilities.ClassNameUtils;
import jsfgenerator.generation.common.utilities.NodeNameUtils;
import jsfgenerator.ui.wizards.EntityDescription;
import jsfgenerator.ui.wizards.EntityFieldDescription;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IRegion;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WildcardType;

/**
 * Utility class to parse AST elements and find the entities in the java model! Java model can be a project, package or multiple packages,
 * maybe only one class.
 * 
 * @author zoltan verebes
 * 
 */
public final class EntityClassParser {

	private static final String ENTITY_ANNOTATION = "Entity";
	private static final String JAVA_UTIL_COLLECTION = "java.util.Collection";
	private static final String DEFAULT_JAVA_PACKAGE = "java.lang";

	/**
	 * looks up all of the entities from a single file, including embedded classes
	 * 
	 * @param resource
	 * @return
	 */
	public static List<EntityDescription> findEntities(IFile resource) {
		if (resource == null) {
			throw new IllegalArgumentException("Resource parameter cannot be null!");
		}

		// AST representation of the resource file
		ICompilationUnit unit = (ICompilationUnit) JavaCore.create(resource);
		return findEntities(createASTParser(unit));
	}

	/**
	 * looks up all of the entities from a set of files, including embedded classes
	 * 
	 * @param resource
	 * @return
	 */
	public static List<EntityDescription> findEntities(IFile[] resources) {
		List<EntityDescription> descriptions = new ArrayList<EntityDescription>();

		for (IFile file : resources) {
			descriptions.addAll(findEntities(file));
		}

		return descriptions;
	}

	/**
	 * <
	 * 
	 * @param entityDescription
	 * @return
	 */
	public static List<EntityFieldDescription> findEntityFields(TypeDeclaration node) {

		List<EntityFieldDescription> entityFields = new ArrayList<EntityFieldDescription>();
		List<String> imports = getImports(node);

		for (FieldDeclaration field : node.getFields()) {

			String typeName = getTypeName(field.getType(), imports);

			for (Object obj : field.fragments()) {
				if (obj instanceof VariableDeclarationFragment) {
					VariableDeclarationFragment fragment = (VariableDeclarationFragment) obj;

					// check if the field has getter and setter and create EntityField
					if (hasGetter(node.getMethods(), fragment, field.getType())
							&& hasSetter(node.getMethods(), fragment, field.getType())) {
						EntityFieldDescription entityField = createEntityField(fragment.getName().getFullyQualifiedName(),
								typeName);
						entityFields.add(entityField);
					}
				}
			}
		}

		return entityFields;
	}

	// TODO
	public static List<EntityDescription> findEntities(IPackageFragment resource) {
		return null;
	}

	public static List<EntityDescription> findEntities(IPackageFragment[] resources) {
		return null;
	}

	public static List<EntityDescription> findEntities(IProject[] projects) {
		return null;
	}

	public static List<EntityDescription> findEntities(IProject project) {
		return null;
	}

	public static List<String> findPackageNames(String simpleClassName, IJavaProject project) {
		List<String> packageNames = new ArrayList<String>();
		try {
			ITypeHierarchy hierarchy = project.newTypeHierarchy(buildRegion(project), null);
			IType[] types = hierarchy.getAllClasses();

			for (IType type : types) {
				if (type.getElementName().equals(simpleClassName)) {
					packageNames.add(type.getFullyQualifiedName());

				}
			}

		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		return packageNames;
	}

	public static String getPackageName(ASTNode node) {
		CompilationUnit cu = getCompilationUnit(node);
		if (cu == null) {
			return null;
		}
		return cu.getPackage().getName().getFullyQualifiedName();
	}

	@SuppressWarnings("unchecked")
	public static List<String> getImports(ASTNode node) {
		CompilationUnit cu = getCompilationUnit(node);

		if (cu != null) {
			List<ImportDeclaration> declarations = cu.imports();
			List<String> imports = new ArrayList<String>();
			for (ImportDeclaration importDeclaration : declarations) {
				imports.add(importDeclaration.getName().getFullyQualifiedName());
			}

			return imports;
		}

		return null;
	}

	/**
	 * Looks up the types package name by its simple name, imports of the compilation unit and its package<br/>
	 * It tries to find it in the following order:
	 * <ul>
	 * <li>primitive type</li>
	 * <li>by its fully qualified name</li>
	 * <li>in its package</li>
	 * <li>in the imported packages</li>
	 * <li>in java.lang java default package</li>
	 * </ul>
	 * 
	 * It uses the project of the selected entity/entities<br>
	 * It does the same for its generic type list if there is any defined
	 * 
	 * 
	 * TODO: document it in details in long-long pages
	 * 
	 * @param type
	 * @param imports
	 * @return
	 */
	protected static String getTypeName(Type type, List<String> imports) {

		if (type.isPrimitiveType()) {
			return type.toString();
		} else if (type.isQualifiedType()) {
			return ((QualifiedType) type).getQualifier().toString() + "." + type.toString();
		} else if (type.isArrayType()) {
			ArrayType aType = (ArrayType) type;
			return getTypeName(aType.getComponentType(), imports) + "[]";
		} else if (type.isParameterizedType()) {
			ParameterizedType pType = (ParameterizedType) type;
			String pTypeName = getTypeName(pType.getType(), imports);

			if (pType.typeArguments().size() == 0) {
				return pTypeName;
			}

			StringBuffer buf = new StringBuffer(pTypeName);
			buf.append("<");
			for (int i = 0; i < pType.typeArguments().size(); i++) {
				Type argType = (Type) pType.typeArguments().get(i);
				String argName = getTypeName(argType, imports);
				buf.append(argName);
				if (i != pType.typeArguments().size() - 1) {
					buf.append(",");
				}
			}
			buf.append(">");
			return buf.toString();
		} else if (type.isWildcardType()) {
			WildcardType wType = (WildcardType) type;
			if (wType.getBound() == null) {
				return "?";
			} else if (wType.isUpperBound()) {
				return "? extends " + getTypeName(wType.getBound(), imports);
			} else {
				return "? super " + getTypeName(wType.getBound(), imports);
			}
		} else if (type.isSimpleType()) {
			// Simple type

			SimpleType sType = (SimpleType) type;
			IJavaProject project = getProject();

			try {
				// try to find it its full name
				IType foundType = project.findType(sType.getName().getFullyQualifiedName());

				if (foundType != null) {
					return foundType.getFullyQualifiedName();
				}

				// try to found it in its package
				String packageName = getPackageName(type);
				foundType = project.findType(packageName, sType.getName().getFullyQualifiedName());

				if (foundType != null) {
					return foundType.getFullyQualifiedName();
				}

				// try to found it in the imports
				for (String imp : imports) {

					if (ClassNameUtils.getSimpleClassName(imp).equals(sType.getName().getFullyQualifiedName())) {
						foundType = project.findType(imp);

						if (foundType != null) {
							return foundType.getFullyQualifiedName();
						}
					}

					foundType = project.findType(imp, sType.getName().getFullyQualifiedName());

					if (foundType != null) {
						return foundType.getFullyQualifiedName();
					}
				}

				// try to find it in the java.lang package
				foundType = project.findType(DEFAULT_JAVA_PACKAGE, sType.getName().getFullyQualifiedName());

				if (foundType != null) {
					return foundType.getFullyQualifiedName();
				}

			} catch (Exception e) {
				throw new IllegalArgumentException("Type parameter could not be parsed!");
			}
		}

		return null;
	}

	/**
	 * Checks if the passed type name is an implementation of java.util.Collection class
	 * 
	 * @param fullyQualifiedTypeName
	 * @return
	 */
	public static boolean isCollection(String fullyQualifiedTypeName) {
		IJavaProject project = getProject();

		IType foundType;
		try {
			foundType = project.findType(fullyQualifiedTypeName);

			if (foundType == null) {
				return false;
			}
			return Arrays.asList(foundType.getSuperInterfaceNames()).contains(JAVA_UTIL_COLLECTION);

		} catch (JavaModelException e) {
			throw new IllegalArgumentException("FullyQualifiedTypeName parameter could not be parsed!");
		}
	}

	/**
	 * Checks if the passed type name is an annotated by any @Entity annotation
	 * 
	 * @param fullyQualifiedTypeName
	 * @return
	 */
	public static boolean isEntity(String fullyQualifiedTypeName) {
		IJavaProject project = getProject();

		IType foundType;
		try {
			foundType = project.findType(fullyQualifiedTypeName);

			if (foundType == null) {
				return false;
			}

			Set<String> annotations = new HashSet<String>();
			for (IAnnotation annotation : foundType.getAnnotations()) {
				String lastFragment = ClassNameUtils.getSimpleClassName(annotation.getElementName());
				annotations.add(lastFragment);

				// TODO: use full name, find the type and check if it is a real entity annotation
				/*
				 * gather its super types
				 */
				// String fullAnnotationName = getTypeName(annotation.ElementName(), getImports(node));
			}

			return annotations.contains(ENTITY_ANNOTATION);

		} catch (JavaModelException e) {
			throw new IllegalArgumentException("FullyQualifiedTypeName parameter could not be parsed!");
		}
	}

	public static String getParentTypeDeclarationsName(ASTNode node) {
		List<TypeDeclaration> parentTypes = getParentTypeDeclaration(node);

		StringBuffer buf = new StringBuffer();
		for (int i = parentTypes.size() - 1; i >= 0; i--) {
			TypeDeclaration type = parentTypes.get(i);
			buf.append(".");
			buf.append(type.getName().getFullyQualifiedName());
		}

		return buf.toString();
	}

	protected static List<TypeDeclaration> getParentTypeDeclaration(ASTNode node) {
		ASTNode currentNode = node.getParent();
		List<TypeDeclaration> types = new ArrayList<TypeDeclaration>();
		while (!(currentNode instanceof CompilationUnit)) {
			if (currentNode instanceof TypeDeclaration) {
				types.add((TypeDeclaration) currentNode);
			}
			currentNode = currentNode.getParent();
		}

		return types;
	}

	protected static CompilationUnit getCompilationUnit(ASTNode node) {
		ASTNode currentNode = node;
		while (!(currentNode instanceof CompilationUnit) && currentNode.getParent() != null) {
			currentNode = currentNode.getParent();
		}

		return (CompilationUnit) currentNode;
	}

	protected static IRegion buildRegion(IJavaProject project) {
		IRegion region = JavaCore.newRegion();
		/*
		 * add all of the classes and jars in the project
		 */
		try {
			for (IPackageFragmentRoot root : project.getAllPackageFragmentRoots()) {
				region.add(root);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		return region;
	}

	protected static List<EntityDescription> findEntities(ASTParser parser) {
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		// use ASTVisitor to find the entity classes in the tree of java model
		EntityClassASTVisitor visitor = new EntityClassASTVisitor();
		cu.accept(visitor);

		return visitor.getEntityDescriptions();
	}

	protected static boolean hasGetter(MethodDeclaration[] methods, VariableDeclarationFragment fragment, Type type) {
		for (MethodDeclaration method : methods) {
			if (EntityClassParser.isGetterOf(method, fragment, type)) {
				return true;
			}
		}
		return false;
	}

	protected static boolean hasSetter(MethodDeclaration[] methods, VariableDeclarationFragment fragment, Type type) {
		for (MethodDeclaration method : methods) {
			if (EntityClassParser.isSetterOf(method, fragment, type)) {
				return true;
			}
		}
		return false;
	}

	protected static EntityFieldDescription createEntityField(String fieldName, String fieldClass) {
		boolean isColleactionOfEntity = true;
		if (ClassNameUtils.getGenericParameterList(fieldClass).size() != 1) {
			isColleactionOfEntity = false;
		}

		if (isColleactionOfEntity) {
			String baseClassName = ClassNameUtils.removeGenericParameters(fieldClass);
			isColleactionOfEntity &= isCollection(baseClassName);
		}

		if (isColleactionOfEntity) {
			String genericClassName = ClassNameUtils.getGenericParameterList(fieldClass).get(0);
			isColleactionOfEntity &= isEntity(genericClassName);
		}

		return new EntityFieldDescription(fieldName, fieldClass, isColleactionOfEntity);
	}

	/**
	 * method is the getter of fieldName
	 * 
	 * A method is getter of a field called xxx if it is called getXxx(), return type is the same as the field type and has no formal
	 * parameters!
	 * 
	 * @param method
	 * @param field
	 * @return
	 */
	protected static boolean isGetterOf(MethodDeclaration method, VariableDeclarationFragment fragment, Type type) {
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
	 * A method is setter of a field called xxx if it is called setXxx(), return type is void and has exactly one parameter which type is
	 * the same as the field type
	 * 
	 * @param method
	 * @param field
	 * @return
	 */
	protected static boolean isSetterOf(MethodDeclaration method, VariableDeclarationFragment fragment, Type type) {
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

	/**
	 * Builds a name for the getter of the field
	 * 
	 * @param fragment
	 * @return getter method name of the field
	 */
	protected static String getGetterName(VariableDeclarationFragment fragment) {
		if (fragment == null) {
			throw new IllegalArgumentException("Field parameter cannot be null!");
		}

		if (fragment.getName().getFullyQualifiedName() == null || fragment.getName().getFullyQualifiedName().equals("")) {
			throw new IllegalArgumentException("Field parameter is incorrect!");
		}

		return NodeNameUtils.getGetterName(fragment.getName().getFullyQualifiedName());
	}

	/**
	 * Builds a name for the setter of the field
	 * 
	 * @param fragment
	 * @return getter method name of the field
	 */
	protected static String getSetterName(VariableDeclarationFragment fragment) {
		if (fragment == null) {
			throw new IllegalArgumentException("Field parameter cannot be null!");
		}

		if (fragment.getName().getFullyQualifiedName() == null || fragment.getName().getFullyQualifiedName().equals("")) {
			throw new IllegalArgumentException("Field parameter is incorrect!");
		}

		return NodeNameUtils.getSetterName(fragment.getName().getFullyQualifiedName());
	}

	protected static ASTParser createASTParser(ICompilationUnit unit) {
		// JLS3 - it is designed to know all of the Java 5 language elements
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);

		return parser;
	}

	protected static IJavaProject getProject() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		root.getProject();
		return JavaCore.create(root.getProject("proba"));
	}
}
