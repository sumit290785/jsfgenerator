package jsfgenerator.generation.utilities;

import java.util.LinkedList;
import java.util.Queue;

import jsfgenerator.generation.tagmodel.ProxyTag;
import jsfgenerator.generation.tagmodel.Tag;
import jsfgenerator.generation.tagmodel.ProxyTag.ProxyTagType;

/**
 * Utility functions of Tag related things
 * 
 * @author zoltan verebes
 * 
 */
public class Tags {

	public static Tag getProxyTagByType(Tag root, ProxyTagType type) {

		Queue<Tag> queue = new LinkedList<Tag>();
		queue.add(root);

		while (!queue.isEmpty()) {
			Tag tag = queue.remove();

			if (tag instanceof ProxyTag && type.equals(((ProxyTag) tag).getType())) {
				return tag;
			}

			queue.addAll(tag.getChildren());
		}

		return null;
	}

}
