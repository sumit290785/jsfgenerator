package jsfgenerator.entitymodel.utilities;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.Type;

/**
 * Try to find the getters and setters of a field in an AST!
 * 
 * @author zoltan verebes
 * 
 */
public class MethodVisitor extends ASTVisitor {

	private String fieldName;

	private Type type;

	private boolean getterFound = false;
	private boolean setterFound = false;

	public MethodVisitor(String fieldName, Type type) {
		if (fieldName == null || fieldName.equals("")) {
			throw new IllegalArgumentException("Field name must be specified!");
		}

		if (type == null) {
			throw new IllegalArgumentException("Type must be specified!");
		}
		this.fieldName = fieldName;
		this.type = type;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		String methodName = node.getName().getFullyQualifiedName();

		getterFound = getterFound
				|| (methodName.equals(getGetterName()) && node.getReturnType2() != null && node.getReturnType2()
						.equals(type));

		if (node.getReturnType2().isPrimitiveType()) {
			PrimitiveType returnType = (PrimitiveType) node.getReturnType2();
			setterFound = setterFound
					|| (methodName.equals(getSetterName()) && returnType.getPrimitiveTypeCode().equals(
							PrimitiveType.VOID));
		}

		return !getterFound || !setterFound;
	}

	private String getGetterName() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("get");

		// there is at least one character in the string, because of the if
		// statement in the constructor
		char firstCharacter = fieldName.charAt(0);
		buffer.append(Character.toUpperCase(firstCharacter));
		if (fieldName.length() > 1) {
			buffer.append(fieldName.substring(1));
		}
		return buffer.toString();
	}

	private String getSetterName() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("set");

		// there is at least one character in the string, because of the if
		// statement in the constructor
		char firstCharacter = fieldName.charAt(0);
		buffer.append(Character.toUpperCase(firstCharacter));

		if (fieldName.length() > 1) {
			buffer.append(fieldName.substring(1));
		}
		return buffer.toString();
	}

	public boolean hasGetter() {
		return getterFound;
	}

	public boolean hasSetter() {
		return setterFound;
	}

}
