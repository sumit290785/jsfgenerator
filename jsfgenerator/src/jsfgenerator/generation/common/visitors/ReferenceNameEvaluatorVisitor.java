package jsfgenerator.generation.common.visitors;

import java.util.Arrays;
import java.util.List;

import jsfgenerator.generation.common.GenerationException;
import jsfgenerator.generation.common.utilities.StringUtils;
import jsfgenerator.generation.view.PlaceholderTagNode;
import jsfgenerator.generation.view.StaticTagNode;
import jsfgenerator.generation.view.AbstractTagNode;
import jsfgenerator.generation.view.parameters.TagAttribute;
import jsfgenerator.generation.view.parameters.TagAttribute.TagParameterType;

public class ReferenceNameEvaluatorVisitor extends AbstractVisitor<AbstractTagNode> {

	public static enum ExpressionType {
		ENTITY_FIELD, SAVE, DELETE, REFRESH, ADD, REMOVE, METHOD_INVOCATION
	}

	private static final String SEPARATOR = ".";

	private static final String EXPRESSION_PREFIX = "#{";
	private static final String EXPRESSION_POSTFIX = "}";

	private List<String> args;

	private String namespace;

	private String[] params;

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

			if (args.size() != 1) {
				throw new GenerationException("Number of arguments is insufficient!");
			}

			String fieldName = args.get(0);

			StringBuffer buffer = new StringBuffer();
			buffer.append(EXPRESSION_PREFIX);
			buffer.append(namespace);
			buffer.append(SEPARATOR);
			buffer.append(fieldName);
			buffer.append(EXPRESSION_POSTFIX);
			attribute.setValue(buffer.toString());
			return;
		}

		if (ExpressionType.SAVE.equals(type) || ExpressionType.DELETE.equals(type) || ExpressionType.ADD.equals(type)
				|| ExpressionType.REFRESH.equals(type)) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(EXPRESSION_PREFIX);
			buffer.append(namespace);
			buffer.append(SEPARATOR);
			buffer.append(getFunction(type));
			buffer.append(EXPRESSION_POSTFIX);
			attribute.setValue(buffer.toString());
			return;
		}

		if (ExpressionType.METHOD_INVOCATION.equals(type)) {
			if (params.length != 2) {
				throw new GenerationException(
						"Number of params is insufficient! Method incocation requires 2 params: viewId and function name");
			}

			String viewId = params[0];
			String methodName = params[1];

			StringBuffer buffer = new StringBuffer();
			buffer.append(EXPRESSION_PREFIX);
			buffer.append(viewId);
			buffer.append(SEPARATOR);
			buffer.append(methodName + "()");
			buffer.append(EXPRESSION_POSTFIX);
			attribute.setValue(buffer.toString());
			return;
		}

	}

	private String getFunction(ExpressionType type) {
		StringBuffer buffer = new StringBuffer();
		if (ExpressionType.SAVE.equals(type)) {
			buffer.append("save");
		} else if (ExpressionType.DELETE.equals(type)) {
			buffer.append("delete");
		} else if (ExpressionType.REFRESH.equals(type)) {
			buffer.append("reload");
		} else if (ExpressionType.ADD.equals(type)) {
			buffer.append("newInstance");
		}

		buffer.append("(");

		if (params != null) {
			buffer.append(StringUtils.toCSV(params));
		}

		buffer.append(")");
		return buffer.toString();
	}

	public void setParams(String... params) {
		this.params = params;
	}

	public String[] getParams() {
		return params;
	}

}