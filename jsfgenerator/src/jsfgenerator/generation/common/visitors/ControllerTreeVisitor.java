package jsfgenerator.generation.common.visitors;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jsfgenerator.generation.controller.ControllerTree;
import jsfgenerator.generation.controller.nodes.ClassControllerNode;
import jsfgenerator.generation.controller.nodes.ControllerNode;
import jsfgenerator.generation.controller.nodes.FieldControllerNode;
import jsfgenerator.generation.controller.nodes.FunctionControllerNode;
import jsfgenerator.generation.controller.utilities.ControllerNodeUtils;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;

public class ControllerTreeVisitor extends AbstractVisitor<ControllerNode> {

	private static class ImportDeclarationVisitor extends AbstractVisitor<ControllerNode> {

		private Set<String> imports = new HashSet<String>();

		@Override
		public boolean visit(ControllerNode node) {
			imports.addAll(node.getRequiredImports());
			return true;
		}

		public List<String> getSortedImports() {
			List<String> sortedImports = Arrays.asList(imports.toArray(new String[0]));
			Collections.sort(sortedImports);
			return sortedImports;
		}

	}

	private AST ast;
	private CompilationUnit unit;
	private TypeDeclaration rootType;

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

	@SuppressWarnings("unchecked")
	private void addFunctionDeclaration(FunctionControllerNode node) {
		MethodDeclaration methodDeclaration = ast.newMethodDeclaration();
		methodDeclaration.setConstructor(false);
		methodDeclaration.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		methodDeclaration.setName(ast.newSimpleName(ControllerNodeUtils.getSimpleClassName(node.getFunctionName())));

		Type returnType = (node.getReturnType() == null) ? ast.newPrimitiveType(PrimitiveType.VOID) : ast
				.newSimpleType(ast.newSimpleName(ControllerNodeUtils.getSimpleClassName(node.getReturnType())));
		methodDeclaration.setReturnType2(returnType);

		for (String parameterName : node.getParameters()) {
			String type = node.getType(parameterName);
			TypeParameter typeParameter = ast.newTypeParameter();
			typeParameter.setName(ast.newSimpleName(ControllerNodeUtils.getSimpleClassName(type)));
			methodDeclaration.parameters().add(typeParameter);
		}

		rootType.bodyDeclarations().add(methodDeclaration);

	}

	@SuppressWarnings("unchecked")
	private void addFieldDeclaration(FieldControllerNode node) {
		VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName(node.getFieldName()));

		FieldDeclaration fd = ast.newFieldDeclaration(vdf);

		Type type = ast.newSimpleType(ast.newSimpleName(ControllerNodeUtils.getSimpleClassName(node.getClassName())));
		/*
		 * TODO: generic ParameterizedType
		 * 
		 * ptype = ast.newParameterizedType(type); ptype.typeArguments().add(e);
		 * fd.setType(ptype);
		 */
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

		// TODO: add interfaces
		rootType.setInterface(false);
		rootType.modifiers().add(ast.newModifier(ModifierKeyword.PRIVATE_KEYWORD));
		rootType.setName(ast.newSimpleName(ControllerNodeUtils.getSimpleClassName(node.getClassName())));

		unit.types().add(rootType);

	}

	@SuppressWarnings("unchecked")
	private void addImports(List<String> sortedImports) {
		for (String imp : sortedImports) {
			ImportDeclaration importDeclaration = ast.newImportDeclaration();
			importDeclaration.setName(getQualifiedName(imp));
			importDeclaration.setOnDemand(true);
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

}
