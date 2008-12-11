package jsfgenerator.generation.common.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to parse class name including fully qualified names, simple short names, generic parameters, etc.
 * 
 * @author zoltan verebes
 * 
 */
public final class ClassNameUtils {

	/*
	 * regular expression for parsing a fully qualified java class name with generic parameter list
	 */
	private static Pattern fullyQualifiedPatttern = Pattern.compile("^((?:[A-Za-z][A-Za-z0-9_]*\\.)*)([A-Za-z0-9]+)($|<((([^>,]+),?)+)>)");

	/**
	 * add generic parameters to a class name
	 * 
	 * @param className
	 * @param param
	 * @return class name with the generic parameters
	 */
	public static String addGenericParameter(String className, String... params) {

		if (params.length == 0) {
			return className;
		}

		Matcher matcher = getMatcher(className);
		ArrayList<String> parameters = new ArrayList<String>(getGenericParameterList(className));
		for (String param : params) {
			parameters.add(param);
		}

		StringBuffer buffer = new StringBuffer();
		buffer.append(matcher.group(1));
		buffer.append(matcher.group(2));

		buffer.append("<");

		Iterator<String> it = parameters.iterator();
		while (it.hasNext()) {
			buffer.append(it.next());
			if (it.hasNext()) {
				buffer.append(",");
			}
		}
		buffer.append(">");

		return buffer.toString();
	}

	/**
	 * Parses a fully qualified class name and returns its package part if it exists. It can handle generic parameters, too.
	 * 
	 * @param fullyQualifiedName
	 * @return package part of the fully qualified class name or empty when it is not defined
	 */
	public static String getPackageName(String fullyQualifiedName) {
		Matcher matcher = getMatcher(fullyQualifiedName);
		String packageName = matcher.group(1);
		return packageName.equals("") ? packageName : packageName.substring(0, packageName.length() - 1);
	}

	/**
	 * Parses a fully qualified class name and returns its class part. It can handle generic parameters, too.
	 * 
	 * @param fullyQualifiedName
	 * @return simple class name part of a fully qualified class name
	 */
	public static String getSimpleClassName(String fullyQualifiedName) {
		Matcher matcher = getMatcher(fullyQualifiedName);
		return matcher.group(2);
	}

	/**
	 * Removes generic parameters from the end of the class name if there is any. It can be used as import for example
	 * 
	 * @param className
	 *            with or without generic parameter definitions
	 * @return fully qualified class name without generic parameters
	 */
	public static String removeGenericParameters(String className) {
		Matcher matcher = getMatcher(className);
		return matcher.group(1) + matcher.group(2);
	}

	/**
	 * Parses a fully qualified class name and returns its generic parameter list if it exists
	 * 
	 * @param className
	 * @return generic parameter list of the class name or an empty list when there is not any generic parameter defined
	 */
	public static List<String> getGenericParameterList(String className) {
		Matcher matcher = getMatcher(className);

		if (matcher.groupCount() < 4 || matcher.group(4) == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(matcher.group(4).split("[,]"));
	}

	private static Matcher getMatcher(String className) {
		if (className == null || className.equals("")) {
			throw new IllegalArgumentException("Class name parameter cannot be null!");
		}

		Matcher matcher = fullyQualifiedPatttern.matcher(className);

		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid class name!");
		}

		return matcher;
	}
}
