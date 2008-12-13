package jsfgenerator.generation.common.visitors;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jsfgenerator.generation.common.INameConstants;
import jsfgenerator.generation.common.utilities.AnnotationNameUtils;
import jsfgenerator.generation.common.utilities.ClassNameUtils;
import jsfgenerator.generation.common.utilities.NodeNameUtils;
import jsfgenerator.generation.common.utilities.AnnotationNameUtils.Pair;
import jsfgenerator.generation.controller.ControllerTree;
import jsfgenerator.generation.controller.blockimplementation.BlockImplementationFactory;
import jsfgenerator.generation.controller.nodes.ClassControllerNode;
import jsfgenerator.generation.controller.nodes.ControllerNode;
import jsfgenerator.generation.controller.nodes.FieldControllerNode;
import jsfgenerator.generation.controller.nodes.FunctionControllerNode;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;

/**
 * Visits the elements of the controller tree and creates classes, fields and functions. It uses eclipse specific AST to do its job
 * 
 * @author zoltan verebes
 * 
 */
public class ControllerTreeVisitor extends AbstractVisitor<ControllerNode> {

	/**
	 * Visits all of the nodes in the controller tree and gather all of the required import declarations.
	 * 
	 * @author zoltan verebes
	 * 
	 */
	protected static class ImportDeclarationVisitor extends AbstractVisitor<ControllerNode> {

		private Set<String> imports = new HashSet<String>();

		/*
		 * (non-Javadoc)
		 * 
		 * @seejsfgenerator.generation.common.visitors.AbstractVisitor#visit( jsfgenerator.generation.common.Node)
		 */
		@Override
		public boolean visit(ControllerNode node) {
			imports.addAll(node.getRequiredImports());
			return true;
		}

		/**
		 * 
		 * @return collection of imports required by the elements of the controller tree. list is sorted by alphabetical order
		 */
		public List<String> getSortedImports() {
			List<String> sortedImports = Arrays.asList(imports.toArray(new String[0]));
			Collections.sort(sortedImports);
			return sortedImports;
		}

	}

	private AST ast;
	private CompilationUnit unit;
	private TypeDeclaration rootType;
	private String rootName;

	public ControllerTreeVisitor(ControllerTree tree) {
		ast = AST.newAST(AST.JLS3);
		unit = ast.newCompilationUnit();
		ImportDeclarationVisitor importVisitor = new ImportDeclarationVisitor();
		tree.apply(importVisitor);
		addImports(importVisitor.getSortedImports());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.common.visitors.AbstractVisitor#visit(jsfgenerator .generation.common.Node)
	 */
	@Override
	public boolean visit(ControllerNode node) {

		if (node instanceof ClassControllerNode) {
			addClassDeclaraton((ClassControllerNode) node);
		} else if (node instanceof FieldControllerNode) {
			addFieldDeclaration((FieldControllerNode) node);
		} else if (node instanceof FunctionControllerNode) {
			addFunctionDeclaration((FunctionControllerNode) node);
		}

		return true;
	}

	public CompilationUnit getCompilationUnit() {
		return unit;
	}

	/**
	 * domain class name in the compilation unit.
	 * 
	 * @return
	 */
	public String getRootClassName() {
		return rootName;
	}

	@SuppressWarnings("unchecked")
	private void addFunctionDeclaration(FunctionControllerNode node) {
		MethodDeclaration methodDeclaration = ast.newMethodDeclaration();
		methodDeclaration.setConstructor(false);

		/*
		 * add annotations
		 */
		for (String annotationText : node.getAnnotations()) {
			Annotation annotation = createAnnotation(annotationText);
			methodDeclaration.modifiers().add(annotation);
		}

		methodDeclaration.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		methodDeclaration.setName(ast.newSimpleName(ClassNameUtils.getSimpleClassName(node.getFunctionName())));

		Type returnType = (node.getReturnType() == null) ? ast.newPrimitiveType(PrimitiveType.VOID)
				: createParameterizedType(node.getReturnType());
		methodDeclaration.setReturnType2(returnType);

		for (String parameterName : node.getParameterNames()) {
			String type = node.getType(parameterName);
			SingleVariableDeclaration singleVariableDecl = ast.newSingleVariableDeclaration();
			// singleVariableDecl.setType(ast.newSimpleType(getQualifiedName(ControllerNodeUtils.getSimpleClassName(type))));
			singleVariableDecl.setType(createParameterizedType(type));
			singleVariableDecl.setName(ast.newSimpleName(parameterName));

			methodDeclaration.parameters().add(singleVariableDecl);
		}

		// body implementation
		methodDeclaration.setBody(BlockImplementationFactory.createBlock(ast, node.getType(), node.getArguments()));

		rootType.bodyDeclarations().add(methodDeclaration);
	}

	@SuppressWarnings("unchecked")
	private void addFieldDeclaration(FieldControllerNode node) {
		VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName(node.getFieldName()));

		FieldDeclaration fd = ast.newFieldDeclaration(vdf);

		Type type = createParameterizedType(node.getClassName());
		fd.setType(type);

		/*
		 * add annotations
		 */
		for (String annotationText : node.getAnnotations()) {
			Annotation annotation = createAnnotation(annotationText);
			fd.modifiers().add(annotation);
		}

		fd.modifiers().add(ast.newModifier(ModifierKeyword.PRIVATE_KEYWORD));
		rootType.bodyDeclarations().add(fd);
	}

	@SuppressWarnings("unchecked")
	private void addClassDeclaraton(ClassControllerNode node) {
		PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
		packageDeclaration.setName(getQualifiedName(node.getPackageName()));
		unit.setPackage(packageDeclaration);

		rootType = ast.newTypeDeclaration();
		rootName = ClassNameUtils.getSimpleClassName(node.getClassName());

		/*
		 * add comment
		 */
		Javadoc doc = ast.newJavadoc();
		TagElement tag = ast.newTagElement();
		TextElement commentGenCode = ast.newTextElement();
		commentGenCode.setText("Generated code<br>");
		tag.fragments().add(commentGenCode);

		TextElement commentControllerClass = ast.newTextElement();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		commentControllerClass.setText("Controller class for view: "
				+ NodeNameUtils.removePostfixFromEntityPageClassName(rootName) + "<br>\n* Date of generation: "
				+ dateFormat.format(new Date()));
		tag.fragments().add(commentControllerClass);
		doc.tags().add(tag);

		TagElement generatedTag = ast.newTagElement();
		generatedTag.setTagName("@generated");
		doc.tags().add(generatedTag);

		rootType.setJavadoc(doc);

		// it sets the flag if the compilation unit is an interface or a class
		rootType.setInterface(false);

		/*
		 * add annotations
		 */
		for (String annotationText : node.getAnnotations()) {
			Annotation annotation = createAnnotation(annotationText);
			rootType.modifiers().add(annotation);
		}

		// class is public
		rootType.modifiers().add(ast.newModifier(ModifierKeyword.PUBLIC_KEYWORD));
		rootType.setName(ast.newSimpleName(rootName));

		/*
		 * set its superclass
		 */
		if (node.getSuperClassName() != null && !node.getSuperClassName().equals("")) {
			Type superClassType = createParameterizedType(node.getSuperClassName());
			rootType.setSuperclassType(superClassType);
		}

		/*
		 * add the interfaces to the class declaration
		 */
		for (String interfaceName : node.getInterfaces()) {
			Type interfaceType = ast.newSimpleType(getQualifiedName(ClassNameUtils.getSimpleClassName(interfaceName)));
			rootType.superInterfaceTypes().add(interfaceType);
		}

		unit.types().add(rootType);
	}

	private Annotation createAnnotation(String annotation) {

		// TODO: implement other annotation types
		List<Pair> keyValuePairs = AnnotationNameUtils.getKeyValuePairs(annotation);

		Annotation ann = null;
		if (keyValuePairs == null || keyValuePairs.isEmpty()) {
			ann = ast.newMarkerAnnotation();
		}

		String typeName = AnnotationNameUtils.getSimpleAnnotationName(annotation);
		ann.setTypeName(ast.newSimpleName(typeName));
		return ann;
	}

	@SuppressWarnings("unchecked")
	private void addImports(List<String> sortedImports) {
		for (String imp : sortedImports) {

			// do not add java.lang import
			if (!INameConstants.JAVA_DEFAULT_PACKAGE.equals(ClassNameUtils.getPackageName(imp))) {
				ImportDeclaration importDeclaration = ast.newImportDeclaration();
				importDeclaration.setName(getQualifiedName(imp));
				unit.imports().add(importDeclaration);
			}
		}
	}

	private Name getQualifiedName(String name) {
		String[] parts = name.split("[.]");
		if (parts.length == 0) {
			return null;
		}

		Name qn = ast.newSimpleName(parts[0]);
		for (int i = 1; i < parts.length; i++) {
			qn = ast.newQualifiedName(qn, ast.newSimpleName(parts[i]));
		}

		return qn;
	}

	@SuppressWarnings("unchecked")
	protected Type createParameterizedType(String fullyQualifiedClassName) {
		String className = ClassNameUtils.getSimpleClassName(ClassNameUtils.removeGenericParameters(fullyQualifiedClassName));
		Name name = ast.newName(className);
		List<String> params = ClassNameUtils.getGenericParameterList(fullyQualifiedClassName);

		Type type = ast.newSimpleType(name);
		if (params.size() == 0) {
			return type;
		}

		ParameterizedType ptype = ast.newParameterizedType(type);

		for (String paramName : params) {
			String genericClassName = ClassNameUtils.getSimpleClassName(ClassNameUtils.removeGenericParameters(paramName));
			Type paramType = ast.newSimpleType(ast.newName(genericClassName));
			ptype.typeArguments().add(paramType);
		}

		return ptype;
	}

}
