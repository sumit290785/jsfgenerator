package jsfgenerator.generation.common.treebuilders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class ResourceBundleBuilder {

	private static ResourceBundleBuilder instance;

	private Set<String> keys = new HashSet<String>();

	private ResourceBundleBuilder() {

	}

	public static ResourceBundleBuilder getInstance() {
		if (instance == null) {
			instance = new ResourceBundleBuilder();
		}

		return instance;
	}

	public void clear() {
		keys.clear();
	}

	public void addKey(String key) {
		keys.add(key);
	}

	public InputStream getMessageInputStream() {
		StringBuffer buffer = new StringBuffer();

		for (String key : keys) {
			buffer.append(key);
			buffer.append("=");

			String value = (key.lastIndexOf(".") == -1) ? key : key.substring(key.lastIndexOf(".") + 1);
			buffer.append(value);
			buffer.append("\n");
		}

		return new ByteArrayInputStream(buffer.toString().getBytes());
	}

}
