package jsfgenerator.generation.common.visitors;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jsfgenerator.generation.common.utilities.ClassNameUtils;
import jsfgenerator.generation.controller.ControllerTree;
import jsfgenerator.generation.controller.nodes.BlockImplementationFactory;
import jsfgenerator.generation.controller.nodes.ClassControllerNode;
import jsfgenerator.generation.controller.nodes.ControllerNode;
import jsfgenerator.generation.controller.nodes.FieldControllerNode;
import jsfgenerator.generation.controller.nodes.FunctionControllerNode;

import org.eclipse.jdt.core.dom.AST;
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
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;

/**
 * Visits the elements of the controller tree and creates classes, fields and
 * functions. It uses eclipse specific AST to do its job
 * 
 * @author zoltan verebes
 * 
 */
public class ControllerTreeVisitor extends AbstractVisitor<ControllerNode> {

	/**
	 * Visits all of the nodes in the controller tree and gather all of the
	 * required import declarations.
	 * 
	 * @author zoltan verebes
	 * 
	 */
	protected static class ImportDeclarationVisitor extends AbstractVisitor<ControllerNode> {

		private Set<String> imports = new HashSet<String>();

		/*
		 * (non-Javadoc)
		 * 
		 * @seejsfgenerator.generation.common.visitors.AbstractVisitor#visit(
		 * jsfgenerator.generation.common.Node)
		 */
		@Override
		public boolean visit(ControllerNode node) {
			imports.addAll(node.getRequiredImports());
			return true;
		}

		/**
		 * 
		 * @return collection of imports required by the elements of the
		 *         controller tree. list is sorted by alphabetical order
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
	 * @see
	 * jsfgenerator.generation.common.visitors.AbstractVisitor#visit(jsfgenerator
	 * .generation.common.Node)
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
		methodDeclaration.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		methodDeclaration.setName(ast.newSimpleName(ClassNameUtils.getSimpleClassName(node.getFunctionName())));

		Type returnType = (node.getReturnType() == null) ? ast.newPrimitiveType(PrimitiveType.VOID) : createParameterizedType(node
				.getReturnType(), false);
		methodDeclaration.setReturnType2(returnType);

		for (String parameterName : node.getParameterNames()) {
			String type = node.getType(parameterName);
			SingleVariableDeclaration singleVariableDecl = ast.newSingleVariableDeclaration();
			//singleVariableDecl.setType(ast.newSimpleType(getQualifiedName(ControllerNodeUtils.getSimpleClassName(type))));
			singleVariableDecl.setType(createParameterizedType(type, false));
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

		Type type = createParameterizedType(node.getClassName(), false);
		fd.setType(type);
		fd.modifiers().add(ast.newModifier(ModifierKeyword.PRIVATE_KEYWORD));
		rootType.bodyDeclarations().add(fd);
	}

	@SuppressWarnings("unchecked")
	private void addClassDeclaraton(ClassControllerNode node) {
		PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
		packageDeclaration.setName(getQualifiedName(node.getPackageName()));
		unit.setPackage(packageDeclaration);

		rootType = ast.newTypeDeclaration();

		/*
		 * add comment
		 */
		Javadoc doc = ast.newJavadoc();
		TagElement comment = ast.newTagElement();
		comment.setTagName("Generated code:  Controller class for view: TODO");
		doc.tags().add(comment);
		rootType.setJavadoc(doc);

		// it sets the flag if the compilation unit is an interface or a class
		rootType.setInterface(false);
		// class is public
		rootType.modifiers().add(ast.newModifier(ModifierKeyword.PUBLIC_KEYWORD));
		rootType.setName(ast.newSimpleName(ClassNameUtils.getSimpleClassName(node.getClassName())));
		rootName = ClassNameUtils.getSimpleClassName(node.getClassName());

		/*
		 * set its superclass
		 */
		if (node.getSuperClassName() != null || !node.getSuperClassName().equals("")) {
			Type superClassType = createParameterizedType(node.getSuperClassName(), false);
			rootType.setSuperclassType(superClassType);
		}

		/*
		 * add the interfaces to the class declaration
		 */
		for (String interfaceName : node.getInterfaces()) {
			Type interfaceType = ast.newSimpleType(getQualifiedName(ClassNameUtils.getSimpleClassName(interfaceName)));
			rootType.superInterfaceTypes().add(interfaceType);
		}
		
		//TODO: add interface functions

		unit.types().add(rootType);
	}

	@SuppressWarnings("unchecked")
	private void addImports(List<String> sortedImports) {
		for (String imp : sortedImports) {
			ImportDeclaration importDeclaration = ast.newImportDeclaration();
			importDeclaration.setName(getQualifiedName(imp));
			unit.imports().add(importDeclaration);
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
	
	protected Type createParameterizedType(String className, boolean isQualified) {
		Name name;
		if (isQualified) {
			name = getQualifiedName(className);
		} else {
			String simpleName = ClassNameUtils.getSimpleClassName(className);
			name = ast.newSimpleName(simpleName);
		}
		
		List<String> params = ClassNameUtils.getGenericParameterList(className);

		Type type = ast.newSimpleType(name);

		if (params.size() == 0) {
			return type;
		}

		ParameterizedType ptype = ast.newParameterizedType(type);

		for (String paramName : params) {
			Type paramType = ast.newSimpleType(ast.newSimpleName(paramName));
			ptype.typeArguments().add(paramType);
		}

		return ptype;
	}
	
}
