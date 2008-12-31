package jsfgenerator.generation.controller.blockimplementation;

import java.util.List;

import jsfgenerator.generation.common.INameConstants;
import jsfgenerator.generation.common.utilities.ClassNameUtils;
import jsfgenerator.generation.common.utilities.NodeNameUtils;
import jsfgenerator.generation.controller.FunctionType;
import jsfgenerator.generation.controller.blockimplementation.InitStatementWrapper.EditorType;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeLiteral;

/**
 * It services EJB3 specific code. It implies that if the generated application is dropped into a JEE container it can operate on the
 * database with good data source configuration.
 * 
 * @author zoltan verebes
 * 
 */
public final class BlockImplementationFactory {

	@SuppressWarnings("unchecked")
	public static Block createBlock(AST ast, FunctionType type, Object[] args) {
		if (FunctionType.EMPTY.equals(type)) {
			return createEmptyBlock(ast);
		} else if (FunctionType.GETTER.equals(type) || FunctionType.SETTER.equals(type)) {

			if (args.length != 1) {
				throw new IllegalArgumentException("Number of arguments is not satisfying");
			}

			if (!(args[0] instanceof String)) {
				throw new IllegalArgumentException("Type of the only argument is not correct. Required: String");
			}

			if (FunctionType.GETTER.equals(type)) {
				return createGetterBlock(ast, (String) args[0]);
			} else {
				return createSetterBlock(ast, (String) args[0]);
			}

		} else if (FunctionType.INIT.equals(type)) {

			if (args.length != 1) {
				throw new IllegalArgumentException("Number of arguments is not satisfying");
			}

			return createInitBlock(ast, (List<InitStatementWrapper>) args[0]);
		} else if (FunctionType.CLASS_GETTER.equals(type)) {
			if (args.length != 1) {
				throw new IllegalArgumentException("Number of arguments is not satisfying");
			}

			if (!(args[0] instanceof String)) {
				throw new IllegalArgumentException("Type of the only argument is not correct. Required: String");
			}

			return createClassGetterBlock(ast, (String) args[0]);
		}

		return null;
	}

	/**
	 * Empty block is not the same as if the block is null
	 * 
	 * @param ast
	 * @return
	 */
	protected static Block createEmptyBlock(AST ast) {
		return ast.newBlock();
	}

	/**
	 * creates a getter block for the specified field
	 * 
	 * @param ast
	 * @param fieldName
	 * @return getter block
	 */
	@SuppressWarnings("unchecked")
	protected static Block createGetterBlock(AST ast, String fieldName) {
		Block block = createEmptyBlock(ast);

		ReturnStatement returnStatement = ast.newReturnStatement();
		SimpleName name = ast.newSimpleName(fieldName);
		returnStatement.setExpression(name);
		block.statements().add(returnStatement);

		return block;
	}

	/**
	 * creates a setter block for the specified field
	 * 
	 * @param ast
	 * @param fieldName
	 * @return setter block
	 */
	@SuppressWarnings("unchecked")
	protected static Block createSetterBlock(AST ast, String fieldName) {
		Block block = createEmptyBlock(ast);

		Assignment statement = ast.newAssignment();

		FieldAccess left = ast.newFieldAccess();
		left.setName(ast.newSimpleName(fieldName));
		left.setExpression(ast.newThisExpression());

		statement.setLeftHandSide(left);
		statement.setRightHandSide(ast.newSimpleName(fieldName));

		block.statements().add(ast.newExpressionStatement(statement));

		return block;
	}

	@SuppressWarnings("unchecked")
	protected static Block createInitBlock(AST ast, List<InitStatementWrapper> wrappers) {
		Block block = createEmptyBlock(ast);

		SuperMethodInvocation superMethod = ast.newSuperMethodInvocation();
		superMethod.setName(ast.newSimpleName(INameConstants.ENTIT_PAGE_INIT_FUNCTION));

		block.statements().add(ast.newExpressionStatement(superMethod));

		for (InitStatementWrapper wrapper : wrappers) {
			Assignment statement = ast.newAssignment();
			FieldAccess left = ast.newFieldAccess();
			left.setName(ast.newSimpleName(wrapper.getFieldName()));
			left.setExpression(ast.newThisExpression());
			statement.setLeftHandSide(left);

			Type type;
			if (EditorType.EDIT_HELPER.equals(wrapper.getEditorType())) {
				String editHelperClassName = ClassNameUtils.getSimpleClassName(INameConstants.SIMPLE_FORM_FIELD_CLASS);
				type = ast.newSimpleType(ast.newSimpleName(editHelperClassName));
			} else {
				String listEditHelperClassName = ClassNameUtils.getSimpleClassName(INameConstants.COMPLEX_FORM_FIELD_CLASS);
				type = ast.newSimpleType(ast.newSimpleName(listEditHelperClassName));
			}

			String simpleGenericName = ClassNameUtils.getSimpleClassName(wrapper.getEntityClass());
			ParameterizedType ptype = ast.newParameterizedType(type);
			Type paramType = ast.newSimpleType(ast.newSimpleName(simpleGenericName));
			ptype.typeArguments().add(paramType);
			ClassInstanceCreation classInstance = ast.newClassInstanceCreation();
			classInstance.setType(ptype);

			// classInstance.arguments().add(ast.newSimpleName(INameConstants.ENTITY_PAGE_FIELD_ENTITY_MANAGER));

			// field parameter
			MethodInvocation methodInvocation = ast.newMethodInvocation();
			methodInvocation.setExpression(ast.newSimpleName(INameConstants.DOMAIN_ENTITY_EDIT_HELPER));
			methodInvocation.setName(ast.newSimpleName("getInstance"));

			MethodInvocation methodInvocationOuter = ast.newMethodInvocation();
			methodInvocationOuter.setExpression(methodInvocation);
			String entityFieldName = NodeNameUtils.getGetterName(wrapper.getEntityFieldName());
			methodInvocationOuter.setName(ast.newSimpleName(entityFieldName));
			classInstance.arguments().add(methodInvocationOuter);

			if (!EditorType.EDIT_HELPER.equals(wrapper.getEditorType())) {
				// managed class parameter
				TypeLiteral literal = ast.newTypeLiteral();
				literal.setType(ast.newSimpleType(ast.newSimpleName(simpleGenericName)));
				classInstance.arguments().add(literal);

			}

			statement.setRightHandSide(classInstance);

			block.statements().add(ast.newExpressionStatement(statement));
		}

		return block;
	}

	@SuppressWarnings("unchecked")
	protected static Block createClassGetterBlock(AST ast, String className) {
		Block block = createEmptyBlock(ast);
		TypeLiteral literal = ast.newTypeLiteral();
		String simpleName = ClassNameUtils.getSimpleClassName(className);
		literal.setType(ast.newSimpleType(ast.newSimpleName(simpleName)));

		ReturnStatement returnStatement = ast.newReturnStatement();
		returnStatement.setExpression(literal);
		block.statements().add(returnStatement);
		return block;
	}

}
