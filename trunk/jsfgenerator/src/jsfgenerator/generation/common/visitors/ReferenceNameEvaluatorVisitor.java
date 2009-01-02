package jsfgenerator.generation.common.visitors;

import jsfgenerator.generation.common.treebuilders.ResourceBundleBuilder;
import jsfgenerator.generation.common.utilities.ClassNameUtils;
import jsfgenerator.generation.view.AbstractTagNode;
import jsfgenerator.generation.view.PlaceholderTagNode;
import jsfgenerator.generation.view.StaticTagNode;
import jsfgenerator.generation.view.impl.ViewTemplateConstants;
import jsfgenerator.generation.view.parameters.TagAttribute;
import jsfgenerator.generation.view.parameters.TagAttribute.TagParameterType;

public class ReferenceNameEvaluatorVisitor extends AbstractVisitor<AbstractTagNode> {

	public static enum ExpressionType {
		ENTITY_NAME(ViewTemplateConstants.EXPRESSION_ENTITY_NAME),

		ENTITY_FIELD(ViewTemplateConstants.EXPRESSION_ENTITY_FIELD),

		ENTITY_FIELD_NAME(ViewTemplateConstants.EXPRESSION_ENTITY_FIELD_NAME),

		SAVE(ViewTemplateConstants.EXPRESSION_SAVE),

		REFRESH(ViewTemplateConstants.EXPRESSION_REFRESH),

		ADD(ViewTemplateConstants.EXPRESSION_ADD),

		REMOVE(ViewTemplateConstants.EXPRESSION_REMOVE),

		RESULT_SET(ViewTemplateConstants.EXPRESSION_RESULT_SET);

		private String name;

		private ExpressionType(String name) {
			this.name = name;
		}

		public static ExpressionType getTypeByName(String name) {
			if (name == null || name.equals("")) {
				return null;
			}

			for (ExpressionType type : ExpressionType.values()) {
				if (type.name.equalsIgnoreCase(name)) {
					return type;
				}
			}

			return null;
		}
	}

	private static final String SEPARATOR = ".";

	private static final String EXPRESSION_PREFIX = "#{";
	private static final String EXPRESSION_POSTFIX = "}";

	private String namespace;

	// name of the variable in the repeater
	private String varVariable;

	private String entityName;

	private String entityFieldName;

	public ReferenceNameEvaluatorVisitor(String namespace, String entityName) {
		this.entityName = entityName;
		this.namespace = namespace;
	}

	@Override
	public boolean visit(AbstractTagNode tag) {

		if (tag instanceof PlaceholderTagNode) {
			return true;
		}

		StaticTagNode stag = (StaticTagNode) tag;
		for (TagAttribute attribute : stag.getAttributes()) {
			if (TagParameterType.EXPRESSION.equals(attribute.getType())) {
				try {
					evaluate(attribute);
				} catch (EvaluationException e) {
					System.out.println("The following expression was not evaluated: " + attribute.getValue() + "; message: "
							+ e.getMessage());
				}
			}
		}

		return true;
	}

	private void evaluate(TagAttribute attribute) throws EvaluationException {

		ExpressionType type = ExpressionType.valueOf(attribute.getValue());

		if (type == null) {
			return;
		}

		if (ExpressionType.ENTITY_FIELD.equals(type)) {

			if (entityName == null) {
				throw new EvaluationException("Entity name is required for evaluation!");
			}

			if (entityFieldName == null) {
				throw new EvaluationException("Entity field name is required for evaluation!");
			}

			StringBuffer buffer = new StringBuffer();
			buffer.append(EXPRESSION_PREFIX);
			buffer.append(varVariable == null || varVariable.equals("") ? namespace : varVariable);
			buffer.append(SEPARATOR);
			buffer.append(entityFieldName);
			buffer.append(EXPRESSION_POSTFIX);
			attribute.setValue(buffer.toString());
			attribute.setType(TagParameterType.STATIC);
			return;
		}

		if (ExpressionType.ENTITY_NAME.equals(type)) {

			if (entityName == null) {
				throw new EvaluationException("Entity name is required for evaluation!");
			}

			StringBuffer buffer = new StringBuffer();
			buffer.append(EXPRESSION_PREFIX);
			ResourceBundleBuilder.getInstance().addKey((ClassNameUtils.getSimpleClassName(entityName)).toLowerCase());
			buffer.append(ResourceBundleBuilder.getInstance().getTranslateMethodInvocation(
					ClassNameUtils.getSimpleClassName(entityName)));

			buffer.append(EXPRESSION_POSTFIX);
			attribute.setValue(buffer.toString());
			attribute.setType(TagParameterType.STATIC);

			return;
		}

		if (ExpressionType.ENTITY_FIELD_NAME.equals(type)) {

			if (entityName == null) {
				throw new EvaluationException("Entity name is required for evaluation!");
			}

			if (entityFieldName == null) {
				throw new EvaluationException("Entity field name is required for evaluation!");
			}

			StringBuffer buffer = new StringBuffer();
			buffer.append(EXPRESSION_PREFIX);
			ResourceBundleBuilder.getInstance().addKey(
					(ClassNameUtils.getSimpleClassName(entityName) + "." + entityFieldName).toLowerCase());
			buffer.append(ResourceBundleBuilder.getInstance().getTranslateMethodInvocation(
					ClassNameUtils.getSimpleClassName(entityName), entityFieldName));

			buffer.append(EXPRESSION_POSTFIX);
			attribute.setValue(buffer.toString());
			attribute.setType(TagParameterType.STATIC);

			return;
		}

		if (ExpressionType.SAVE.equals(type) || ExpressionType.REMOVE.equals(type) || ExpressionType.ADD.equals(type)
				|| ExpressionType.REFRESH.equals(type)) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(EXPRESSION_PREFIX);

			buffer.append((ExpressionType.SAVE.equals(type) && namespace != null && namespace.lastIndexOf(".") != -1 && namespace
					.substring(namespace.lastIndexOf(".") + 1).equals("entityEditHelper")) ? namespace.substring(0, namespace
					.lastIndexOf(".")) : namespace);

			buffer.append(SEPARATOR);
			buffer.append(getFunction(type));
			buffer.append(EXPRESSION_POSTFIX);
			attribute.setValue(buffer.toString());
			attribute.setType(TagParameterType.STATIC);
			return;
		}

		if (ExpressionType.RESULT_SET.equals(type)) {

			if (entityName == null) {
				throw new EvaluationException("Entity name is required for evaluation!");
			}

			StringBuffer buffer = new StringBuffer();
			buffer.append(EXPRESSION_PREFIX);
			buffer.append(namespace);
			buffer.append(SEPARATOR);
			buffer.append("resultSet");
			buffer.append(EXPRESSION_POSTFIX);
			attribute.setValue(buffer.toString());
			attribute.setType(TagParameterType.STATIC);
			return;
		}

	}

	private void addIndex(StringBuffer buffer) {
		if (varVariable != null) {
			buffer.append("elements");
			buffer.append("[");
			buffer.append(varVariable);
			buffer.append("]");
			buffer.append(SEPARATOR);
		}
	}

	private String getFunction(ExpressionType type) {
		StringBuffer buffer = new StringBuffer();
		if (ExpressionType.SAVE.equals(type)) {
			addIndex(buffer);
			buffer.append("save");
		} else if (ExpressionType.REMOVE.equals(type)) {
			addIndex(buffer);
			buffer.append("remove");
		} else if (ExpressionType.REFRESH.equals(type)) {
			addIndex(buffer);
			buffer.append("reload");
		} else if (ExpressionType.ADD.equals(type)) {
			buffer.append("add");
		}

		return buffer.toString();
	}

	public void setVarVariable(String varVariable) {
		this.varVariable = varVariable;
	}

	public String getVarVariable() {
		return varVariable;
	}

	public void setEntityFieldName(String entityFieldName) {
		this.entityFieldName = entityFieldName;
	}

}
