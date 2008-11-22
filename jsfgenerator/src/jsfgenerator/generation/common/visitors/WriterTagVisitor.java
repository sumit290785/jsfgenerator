package jsfgenerator.generation.common.visitors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jsfgenerator.generation.view.ProxyTag;
import jsfgenerator.generation.view.StaticTag;
import jsfgenerator.generation.view.TagNode;
import jsfgenerator.generation.view.parameters.TagAttribute;

/**
 * 
 * @author zoltan verebes
 *
 */
public class WriterTagVisitor extends AbstractVisitor<TagNode> {
	
	private static final String TAB = "\t";
	private static final String NEWLINE = "\n";
	
	private ByteArrayOutputStream os;
	
	private int depth = 0;
	
	public WriterTagVisitor(ByteArrayOutputStream os) {
		this.os = os;
	}

	@Override
	public boolean visit(TagNode tag) {
		if (tag instanceof ProxyTag) {
			return true;
		}

		StaticTag staticTag = (StaticTag) tag;

		if (staticTag.isLeaf()) {
			StringBuffer buffer = new StringBuffer();
			appendTabs(buffer, depth);
			buffer.append("<");
			buffer.append(getTagAsString(staticTag));
			buffer.append(" />");
			buffer.append(NEWLINE);
			try {
				os.write(buffer.toString().getBytes());
			} catch (IOException e) {
			}
		} else {
			StringBuffer buffer = new StringBuffer();
			appendTabs(buffer, depth);
			buffer.append("<");
			buffer.append(getTagAsString(staticTag));
			buffer.append(">");
			buffer.append(NEWLINE);

			try {
				os.write(buffer.toString().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			depth++;
		}
		
		return true;
	}
	
	@Override
	public void postVisit(TagNode tag) {
		if (tag instanceof ProxyTag) {
			return;
		}

		StaticTag staticTag = (StaticTag) tag;

		if (staticTag.isLeaf()) {
			return;
		}

		depth--;
		StringBuffer buffer = new StringBuffer();

		appendTabs(buffer, depth);
		buffer.append("</");
		buffer.append(staticTag.getName());
		buffer.append(">");
		buffer.append(NEWLINE);

		try {
			os.write(buffer.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ByteArrayOutputStream getOutputStream() {
		return os;
	}
	
	private void appendTabs(StringBuffer buffer, int depth) {
		for (int i = 0; i < depth; i++) {
			buffer.append(TAB);
		}
	}
	
	private String getTagAsString(StaticTag tag) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(tag.getName());
		for (TagAttribute attribute : tag.getAttributes()) {
			buffer.append(" ");
			buffer.append(attribute.getName());
			buffer.append("=\"");
			buffer.append(attribute.getValue());
			buffer.append("\"");
			// TODO: change expressions
		}

		return buffer.toString();
	}

}
