package jsfgenerator.generation.common.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NodeNameUtils {
	
	private static final Pattern namePattern = Pattern.compile("^(([A-Za-z0-9_]+\\.)*)([A-Za-z0-9_]+)");

	private static final String EDITOR_FIELD_POSTFIX = "Editor";
	
	private static final String ENTITY_PAGE_POSTFIX = "Page";
	
	private static final String SAVE_FUNCTION_NAME = "save";
	
	private static final String SETTER_PREFIX = "set";
	private static final String GETTER_PREFIX = "get";

	public static String getUniqueFormName(String... args) {
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < args.length; i++) {
			buffer.append(args[i]);
			if (i != args.length - 1) {
				buffer.append(".");
			}
		}

		return buffer.toString();
	}

	public static String getControllerEditorFieldNameByUniqueName(String uniqueName) {
		return getControllerFieldNameByUniqueName(uniqueName) + EDITOR_FIELD_POSTFIX;
	}
	
	public static String getEntityPageClassNameByUniqueName(String uniqueName) {
		
		if (uniqueName == null || uniqueName.equals("")) {
			return null;
		}
		
		Matcher matcher = namePattern.matcher(uniqueName);

		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid name!");
		}

		return matcher.group(matcher.groupCount()) + ENTITY_PAGE_POSTFIX;
	}

	public static String getControllerFieldNameByUniqueName(String uniqueName) {
		if (uniqueName == null) {
			return null;
		}

		int i = uniqueName.lastIndexOf(".");
		if (i == -1) {
			return uniqueName;
		}

		return uniqueName.substring(i + 1);
	}

	public static String getSetterName(String fieldName) {
		if (fieldName == null || fieldName.equals("")) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append(SETTER_PREFIX);
		buffer.append(fieldName.substring(0, 1).toUpperCase());

		if (fieldName.length() > 1) {
			buffer.append(fieldName.substring(1));
		}

		return buffer.toString();
	}

	public static String getGetterName(String fieldName) {
		if (fieldName == null || fieldName.equals("")) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append(GETTER_PREFIX);
		buffer.append(fieldName.substring(0, 1).toUpperCase());

		if (fieldName.length() > 1) {
			buffer.append(fieldName.substring(1));
		}

		return buffer.toString();
	}

	public static String getAddFunctionName(String collectionFieldName) {
		// TODO
		return "addToAkarmi";
	}

	public static String getRemoveFunctionName(String collectionFieldName) {
		// TODO
		return "removeFromAkarmi";
	}
	
	public static String getSaveFunctionName() {
		return SAVE_FUNCTION_NAME;
	}
	
}
