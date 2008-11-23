package jsfgenerator.generation.common;

import java.io.ByteArrayOutputStream;

import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * Data transfer object of the generated views, controllers and their names
 * which are the same as the names of the files which contain them
 * 
 * @author zoltan verebes
 * 
 */
public class ViewAndControllerDTO {

	private String viewId;

	private ByteArrayOutputStream viewStream;

	private CompilationUnit viewClass;

	private String viewName;

	private String controllerClassName;

	public ViewAndControllerDTO(String viewId) {
		this.viewId = viewId;
	}

	public String getViewId() {
		return viewId;
	}

	public void setViewStream(ByteArrayOutputStream viewStream) {
		this.viewStream = viewStream;
	}

	public ByteArrayOutputStream getViewStream() {
		return viewStream;
	}

	public void setViewClass(CompilationUnit viewClass) {
		this.viewClass = viewClass;
	}

	public CompilationUnit getViewClass() {
		return viewClass;
	}

	public String getViewName() {
		return viewName;
	}

	public String getControllerClassName() {
		return controllerClassName;
	}

	public void setControllerClassName(String controllerClassName) {
		this.controllerClassName = controllerClassName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

}
