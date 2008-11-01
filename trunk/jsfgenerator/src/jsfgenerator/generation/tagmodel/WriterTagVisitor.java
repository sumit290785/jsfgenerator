package jsfgenerator.generation.tagmodel;

import java.io.IOException;
import java.io.OutputStream;

import jsfgenerator.generation.tagmodel.parameters.TagParameter;

/**
 * 
 * @author zoltan verebes
 *
 */
public class WriterTagVisitor extends TagVisitor {
	
	private static final String TAB = "\t";
	private static final String NEWLINE = "\n";
	
	private OutputStream os;
	
	private int depth = 0;
	
	public WriterTagVisitor(OutputStream os) {
		this.os = os;
	}

	@Override
	public boolean visit(Tag tag) {
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
	public void postVisit(Tag tag) {
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
	
	public OutputStream getOutputStream() {
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
		for (TagParameter parameter : tag.getParameters()) {
			buffer.append(" ");
			buffer.append(parameter.getName());
			buffer.append("=\"");
			buffer.append(parameter.getValue());
			buffer.append("\"");
			// TODO: change expressions
		}

		return buffer.toString();
	}

}
