package jsfgenerator.generation.view.impl;

public final class ViewTemplateConstants {

	public static final String ANNOTATION_NS_URI = "http://www.example.org/annotations";

	// xPATH expression for template with an id parameter
	public static final String TEMPLATE_XPATH = "//annotation:template";

	public static final String ROOT_XPATH = "/annotation:view";

	public static final String VARIABLE = "variable";

	public static final String MESSAGE = "message";

	public static final String VAR_ATTRIBUTE = "attribute";

	public static final String EXPRESSION = "expression";

	public static final String EXPRESSION_TYPE = "type";

	public static final String EXPRESSION_FOR = "for";

	public static final String EXPRESSION_ENTITY_FIELD = "entityField";

	public static final String EXPRESSION_ENTITY_FIELD_NAME = "entityFieldName";

	public static final String EXPRESSION_ADD = "add";

	public static final String EXPRESSION_REMOVE = "remove";

	public static final String EXPRESSION_SAVE = "save";

	public static final String EXPRESSION_REFRESH = "refresh";

	public static final String PLACE_HOLDER = "placeHolder";

	public static final String PLACE_HOLDER_FOR = "for";

	public static final String ENTITY_PAGE = "entityPage";

	public static final String ENTITY_LIST_PAGE = "entityListPage";

	public static final String ENTITY_FORM = "entityForm";

	public static final String ENTITY_LIST_FORM = "entityListForm";

	public static final String ENTITY_LIST_ELEMENT = "listElement";

	public static final String INPUT = "input";

	public static final String XMLNS = "xmlns";

	public static String getTemplateXPath(String id) {
		if (id == null || id.equals("")) {
			throw new IllegalArgumentException("Empty id is invalid");
		}

		return getTemplateXPath(id, null);
	}

	public static String getTemplateXPath(String id, String name) {
		if (id == null || id.equals("")) {
			throw new IllegalArgumentException("Empty id is invalid");
		}

		StringBuffer buffer = new StringBuffer(TEMPLATE_XPATH);
		buffer.append("[");
		buffer.append("@id='");
		buffer.append(id);
		buffer.append("'");

		if (name != null && !name.equals("")) {
			buffer.append(" and ");
			buffer.append("@name='");
			buffer.append(name);
			buffer.append("'");
		}

		buffer.append("]");

		return buffer.toString();
	}

}
