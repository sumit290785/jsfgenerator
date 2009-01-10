package jsfgenerator.generation.view.impl;

public final class ViewTemplateConstants {

	public static final String ANNOTATION_NS_URI = "http://www.jsfgen.com/annotations";

	// xPATH expression for template with an id parameter
	public static final String TEMPLATE_XPATH = "//annotation:template";
	
	public static final String PLACEHOLDER_XPATH = "//annotation:placeHolder";
	
	public static final String VARIABLE_XPATH = "//annotation:variable";

	public static final String ROOT_XPATH = "/annotation:view";

	public static final String VARIABLE = "variable";

	public static final String MESSAGE = "message";

	public static final String VAR_ATTRIBUTE = "attribute";

	public static final String EXPRESSION = "expression";

	public static final String EXPRESSION_TYPE = "type";

	public static final String EXPRESSION_FOR = "for";

	public static final String EXPRESSION_ENTITY_FIELD = "entityField";

	public static final String EXPRESSION_ENTITY_FIELD_NAME = "entityFieldName";

	public static final String EXPRESSION_ENTITY_NAME = "entityName";

	public static final String EXPRESSION_ADD = "add";

	public static final String EXPRESSION_REMOVE = "remove";

	public static final String EXPRESSION_SAVE = "save";

	public static final String EXPRESSION_REFRESH = "refresh";

	public static final String EXPRESSION_RESULT_SET = "resultSet";

	public static final String PLACE_HOLDER = "placeHolder";

	public static final String PLACE_HOLDER_FOR = "for";

	public static final String ENTITY_PAGE = "entityPage";

	public static final String ENTITY_LIST_PAGE = "entityListPage";

	public static final String ENTITY_FORM = "entityForm";

	public static final String ENTITY_LIST_FORM = "entityListForm";

	public static final String LIST_COLUMN_DATA = "columnData";

	public static final String LIST_COLLECTION_COLUMN = "collectionColumn";

	public static final String LIST_COLLECTION_COLUMN_DATA = "collectionColumnData";
	
	public static final String LIST_COLUMN_ACTION = "columnAction";
	
	public static final String LIST_COLUMN_HEADER = "columnHeader";
	
	public static final String ACTION_BAR= "actionBar";
	
	public static final String ACTION = "action";

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
	
	public static String getPlaceholderXPath(String id) {
		if (id == null || id.equals("")) {
			throw new IllegalArgumentException("Empty id is invalid");
		}
		
		StringBuffer buffer = new StringBuffer(PLACEHOLDER_XPATH);
		buffer.append("[");
		buffer.append("@for='");
		buffer.append(id);
		buffer.append("'");
		buffer.append("]");
		
		return buffer.toString();
	}
}
