package jsfgenerator.generation.common.utilities;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to parse annotations including fully qualified names, simple short names, key value pairs, etc.
 * 
 * @author zoltan verebes
 * 
 */
public final class AnnotationNameUtils {

	public static class Pair {
	}

	/*
	 * regular expression for parsing a fully qualified java class name with generic parameter list
	 */
	// TODO: handle parameters
	private static Pattern fullyQualifiedAnnotationPatttern = Pattern
			.compile("^(@)((?:[A-Za-z][A-Za-z0-9_]*\\.)*)([A-Za-z0-9]+)$");

	public static String getFullyQualifiedAnnotationClassName(String annotation) {
		Matcher matcher = getMatcher(annotation);

		return matcher.group(2) + matcher.group(3);
	}

	public static String getSimpleAnnotationName(String annotation) {
		Matcher matcher = getMatcher(annotation);

		return matcher.group(3);
	}

	public static String getFullyQualifiedAnnotation(String annotation) {
		Matcher matcher = getMatcher(annotation);

		return matcher.group(1) + matcher.group(2) + matcher.group(3);
	}

	public static List<Pair> getKeyValuePairs(String annotation) {
		// TODO
		return Collections.emptyList();
	}

	private static Matcher getMatcher(String annotation) {
		if (annotation == null || annotation.equals("")) {
			throw new IllegalArgumentException("Annotation parameter cannot be null!");
		}

		Matcher matcher = fullyQualifiedAnnotationPatttern.matcher(annotation);

		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid annotation!");
		}

		return matcher;
	}
	
}
