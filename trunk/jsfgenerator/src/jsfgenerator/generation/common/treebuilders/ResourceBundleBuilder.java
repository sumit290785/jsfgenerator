package jsfgenerator.generation.common.treebuilders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jsfgenerator.generation.common.INameConstants;
import jsfgenerator.generation.common.utilities.StringUtils;

public class ResourceBundleBuilder {

	private static final String MESSAGE_BUNDLE_FUNCTION = "translate";

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
		
		List<String> sortedKeys = new ArrayList<String>(keys);
		Collections.sort(sortedKeys);
		
		for (String key : sortedKeys) {
			buffer.append(key);
			buffer.append("=");

			String value = (key.lastIndexOf(".") == -1) ? key : key.substring(key.lastIndexOf(".") + 1);
			buffer.append(value);
			buffer.append("\n");
		}

		return new ByteArrayInputStream(buffer.toString().getBytes());
	}

	public String getTranslateMethodInvocation(String... args) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(INameConstants.JSFGEN_TAGLIB_XMLNS_PREFIX);
		buffer.append(":");
		buffer.append(MESSAGE_BUNDLE_FUNCTION);
		buffer.append("('");
		buffer.append(StringUtils.toDotSeparatedString(args).toLowerCase());
		buffer.append("')");
		return buffer.toString();
	}

}
