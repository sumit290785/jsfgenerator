package jsfgenerator.generation.controller.nodes;

import java.util.HashSet;
import java.util.Set;

import jsfgenerator.generation.common.utilities.ClassNameUtils;

/**
 * 
 * @author zoltan verebes
 * 
 */
public class FieldControllerNode extends ControllerNode {

	private String fieldName;

	// fully qualified class name of the field
	private String className;
	
	// fully qualified concrete type name of the field
	private String concreteClassName;

	public FieldControllerNode(String fieldName, String className) {
		this.fieldName = fieldName;
		this.className = className;
	}

	public FieldControllerNode(String fieldName, String className, String concreteClassName) {
		this.fieldName = fieldName;
		this.className = className;
		this.concreteClassName = concreteClassName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getConcreteClassName() {
		return concreteClassName;
	}

	public void setConcreteClassName(String concreteClassName) {
		this.concreteClassName = concreteClassName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	/*
	 * (non-Javadoc)
	 * @see jsfgenerator.generation.controller.nodes.ControllerNode#getRequiredImports()
	 */
	@Override
	public Set<String> getRequiredImports() {
		Set<String> imports = new HashSet<String>();
		imports.add(ClassNameUtils.removeGenericParameters(className));
		
		if (concreteClassName != null) {
			imports.add(ClassNameUtils.removeGenericParameters(concreteClassName));
		}
		
		return imports;
	}

}
