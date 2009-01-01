package jsfgenerator.generation.common.visitors;

import java.util.Arrays;
import java.util.List;

import jsfgenerator.generation.common.GenerationException;
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
		ENTITY_FIELD(ViewTemplateConstants.EXPRESSION_ENTITY_FIELD), ENTITY_FIELD_NAME(
				ViewTemplateConstants.EXPRESSION_ENTITY_FIELD_NAME), SAVE(ViewTemplateConstants.EXPRESSION_SAVE), REFRESH(
				ViewTemplateConstants.EXPRESSION_REFRESH), ADD(ViewTemplateConstants.EXPRESSION_ADD), REMOVE(
				ViewTemplateConstants.EXPRESSION_REMOVE);

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

	private List<String> args;

	private String namespace;

	// name of the variable in the repeater
	private String varVariable;

	public ReferenceNameEvaluatorVisitor(String namespace, String... args) {
		this.args = Arrays.asList(args);
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
				evaluate(attribute);
			}
		}

		return true;
	}

	private void evaluate(TagAttribute attribute) {

		ExpressionType type = ExpressionType.valueOf(attribute.getValue());

		if (type == null) {
			return;
		}

		if (ExpressionType.ENTITY_FIELD.equals(type)) {

			if (args.size() == 0) {
				throw new GenerationException("Number of arguments is insufficient!");
			}

			String fieldName = args.get(0);

			StringBuffer buffer = new StringBuffer();
			buffer.append(EXPRESSION_PREFIX);
			buffer.append(varVariable == null || varVariable.equals("") ? namespace : varVariable);
			buffer.append(SEPARATOR);
			buffer.append(fieldName);
			buffer.append(EXPRESSION_POSTFIX);
			attribute.setValue(buffer.toString());
			return;
		}

		if (ExpressionType.ENTITY_FIELD_NAME.equals(type)) {

			if (args.size() != 2) {
				throw new GenerationException("Number of arguments is insufficient!");
			}

			String fieldName = args.get(0);
			String entityClassName = args.get(1);

			StringBuffer buffer = new StringBuffer();
			buffer.append(EXPRESSION_PREFIX);
			ResourceBundleBuilder.getInstance().addKey(
					(ClassNameUtils.getSimpleClassName(entityClassName) + "." + fieldName).toLowerCase());
			buffer.append(ResourceBundleBuilder.getInstance().getTranslateMethodInvocation(
					ClassNameUtils.getSimpleClassName(entityClassName), fieldName));

			buffer.append(EXPRESSION_POSTFIX);
			attribute.setValue(buffer.toString());

			ResourceBundleBuilder.getInstance().addKey(entityClassName.toLowerCase());
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

}
