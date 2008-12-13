package jsfgenerator.generation.controller.blockimplementation;

import jsfgenerator.generation.controller.FunctionType;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;

/**
 * It services EJB3 specific code. It implies that if the generated application is dropped into a JEE container it can operate on the
 * database with good data source configuration.
 * 
 * @author zoltan verebes
 * 
 */
public final class BlockImplementationFactory {

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

		} else if (FunctionType.SAVE.equals(type)) {
			return createSaveBlock(ast);
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

	/**
	 * creates a save block which is applicable for EJB3 specification. It implies that if the generated application is dropped into a JEE
	 * container it can operate on the database with good data source configuration
	 * 
	 * @param ast
	 * @return
	 */
	protected static Block createSaveBlock(AST ast) {
		// TODO: implement
		return createEmptyBlock(ast);
	}
}
