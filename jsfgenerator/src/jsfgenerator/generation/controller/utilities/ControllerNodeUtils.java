package jsfgenerator.generation.controller.utilities;


public final class ControllerNodeUtils {

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
		
		return (beginIndex == -1)  ? simpleName : simpleName.substring(0, beginIndex);
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
	
}
