package jsfgenerator.generation.common.utilities;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsfgenerator.generation.common.INameConstants;

public final class NodeNameUtils {

	private static final Pattern namePattern = Pattern.compile("^(([A-Za-z][A-Za-z0-9_]*\\.)*)([A-Za-z][A-Za-z0-9_]*)");

	public static String getControllerEditorFieldNameByCanonicalName(String uniqueName) {
		return getControllerFieldNameByCanonicalName(uniqueName) + INameConstants.EDITOR_FIELD_POSTFIX;
	}

	public static String getControllerFieldNameByCanonicalName(String uniqueName) {
		if (uniqueName == null || uniqueName.equals("")) {
			return null;
		}

		Matcher matcher = getMatcher(uniqueName);
		return matcher.group(matcher.groupCount());
	}

	public static String getEntityPageClassNameByUniqueName(String uniqueName) {

		if (uniqueName == null || uniqueName.equals("")) {
			return null;
		}

		Matcher matcher = getMatcher(uniqueName);
		return  capitalName(matcher.group(matcher.groupCount())) + INameConstants.ENTITY_PAGE_POSTFIX;
	}
	
	public static String getListPageClassNameByUniqueName(String uniqueName) {

		if (uniqueName == null || uniqueName.equals("")) {
			return null;
		}

		Matcher matcher = getMatcher(uniqueName);
		return  capitalName(matcher.group(matcher.groupCount())) + INameConstants.LIST_PAGE_POSTFIX;
	}
	
	public static String removePostfixFromEntityPageClassName(String className) {
		return className.replace(INameConstants.ENTITY_PAGE_POSTFIX, "");
	}

	public static String getEntityPageClassFileNameByUniqueName(String uniqueName) {
		return getEntityPageClassNameByUniqueName(uniqueName) + "." + INameConstants.CLASS_FILENAME_EXTENSION;
	}

	public static String getEntityPageViewNameByUniqueName(String uniqueName) {
		if (uniqueName == null || uniqueName.equals("")) {
			return null;
		}

		Matcher matcher = getMatcher(uniqueName);
		return matcher.group(matcher.groupCount()) + "." + INameConstants.VIEW_NAME_EXTENSION;
	}
	
	public static String getListPageClassFileNameByUniqueName(String uniqueName) {
		return getListPageClassNameByUniqueName(uniqueName) + "." + INameConstants.CLASS_FILENAME_EXTENSION;
	}

	public static String getListPageViewNameByUniqueName(String uniqueName) {
		if (uniqueName == null || uniqueName.equals("")) {
			return null;
		}

		Matcher matcher = getMatcher(uniqueName);
		return toPlurar(matcher.group(matcher.groupCount())) + "." + INameConstants.VIEW_NAME_EXTENSION;
	}

	public static String getSetterName(String fieldName) {
		if (fieldName == null || fieldName.equals("")) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append(INameConstants.SETTER_PREFIX);
		buffer.append(capitalName(fieldName));
		return buffer.toString();
	}

	public static String getGetterName(String fieldName) {
		if (fieldName == null || fieldName.equals("")) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append(INameConstants.GETTER_PREFIX);
		buffer.append(capitalName(fieldName));
		return buffer.toString();
	}

	public static boolean isValidViewId(String viewId) {

		if (viewId.equals("")) {
			return false;
		}

		Matcher matcher = namePattern.matcher(viewId);
		return matcher.matches();
	}
	
	public static String getResourceBundleName(Locale locale) {
		return "messages_" + locale.getLanguage() + "_" + locale.getCountry() + ".properties";
	}
	

	protected static String capitalName(String name) {
		if (name == null || name.length() == 0) {
			return name;
		}

		StringBuffer buffer = new StringBuffer();
		buffer.append(Character.toUpperCase(name.charAt(0)));

		if (name.length() != 1) {
			buffer.append(name.substring(1));
		}

		return buffer.toString();
	}

	protected static Matcher getMatcher(String name) {
		Matcher matcher = namePattern.matcher(name);

		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid name!");
		}

		return matcher;
	}

	protected static String toPlurar(String word) {
		if (word == null || word.equals("")) {
			return "";
		}
		
		if (word.charAt(word.length() - 1) == 'y') {
			return word.substring(0, word.length() - 2) + "ies";
		}
		
		return word + "s";
	}
}
