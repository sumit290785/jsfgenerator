package jsfgenerator.generation.controller.utilities;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ControllerNodeUtils {

	// regular expression for parsing a fully qualified java class name with
	// generic parameters
	private static Pattern fullyQualifiedPatttern = Pattern
			.compile("^((?:[A-Za-z0-9_]+\\.)*)([A-Za-z0-9]+)($|<((([^>,]+),?)+)>)");

	public static String getPackageName(String fullyQualifiedName) {
		int index = fullyQualifiedName.lastIndexOf(".");
		return (index == -1) ? fullyQualifiedName : fullyQualifiedName.substring(0, index);
	}

	public static String getSimpleClassName(String fullyQualifiedName) {
		int index = fullyQualifiedName.lastIndexOf(".");
		String simpleName = (index == -1) ? fullyQualifiedName : fullyQualifiedName.substring(index + 1);

		/*
		 * remove generic parameters
		 */
		int beginIndex = simpleName.indexOf("<");

		return (beginIndex == -1) ? simpleName : simpleName.substring(0, beginIndex);
	}

	/**
	 * Removes generic parameters from the end of the class name if there is
	 * any. It can be used as import for example
	 * 
	 * @param className
	 *            with or without generic parameter definitions
	 * @return fully qualified class name without generic parameters
	 */
	public static String removeGenericParameters(String className) {
		if (className == null || className.equals("")) {
			throw new IllegalArgumentException("Class name parameter cannot be null!");
		}

		Matcher matcher = fullyQualifiedPatttern.matcher(className);

		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid class name!");
		}

		return matcher.group(1) + matcher.group(2);
	}

	public static String addGenericParameter(String className, String param) {
		int beginIndex = className.indexOf("<");
		int endIndex = className.indexOf(">");
		StringBuffer buffer = new StringBuffer();
		if (beginIndex == -1 && endIndex == -1) {
			buffer.append(className);
			buffer.append("<");
			buffer.append(getSimpleClassName(param));
			buffer.append(">");
		} else if (beginIndex != -1 && endIndex != -1) {
			buffer.append(className.substring(0, beginIndex - 1));
			buffer.append("<");
			buffer.append(className.substring(beginIndex + 1, endIndex - 1));
			buffer.append(",");
			buffer.append(param);
			buffer.append(">");
		}

		return buffer.toString();
	}
	
	public static List<String> getGenericParameterList(String className) {
		Matcher matcher = fullyQualifiedPatttern.matcher(className);

		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid class name!");
		}
		
		if (matcher.groupCount() < 4) {
			return Collections.emptyList();
		}
		
		return Arrays.asList(matcher.group(4).split("[,]"));
	}

	public static void main(String... args) {
		List<String> p = getGenericParameterList("jssdf.sfsf.sfd.sdfsf.xxx<AAAA,AAAB>");
		
		for (String string : p) {
			System.err.println(string);
		}
	}

}
