package jsfgenerator.generation.common.utilities;

import java.util.Arrays;
import java.util.Iterator;

public final class StringUtils {

	public static String toDotSeparatedString(String... args) {
		return concatenate(Arrays.asList(args), ".");
	}
	
	public static String toDotSeparatedString(Iterable<String> args) {
		return concatenate(args, ".");
	}

	public static String toCSV(Iterable<String> iterable) {
		return concatenate(iterable, ", ");
	}
	
	public static String toCSV(String... args) {
		return concatenate(Arrays.asList(args), ", ");
	}

	private static String concatenate(Iterable<String> iterable, String separator) {
		StringBuffer buffer = new StringBuffer();
		Iterator<String> it = iterable.iterator();
		while (it.hasNext()) {
			buffer.append(it.next());
			if (it.hasNext()) {
				buffer.append(separator);
			}
		}

		return buffer.toString();
	}

}
