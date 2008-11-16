package jsfgenerator.generation.controller.nodes;

import java.util.HashSet;
import java.util.Set;

import jsfgenerator.generation.controller.utilities.ControllerNodeUtils;

/**
 * 
 * @author zoltan verebes
 * 
 */
public class ClassControllerNode extends ControllerNode {

	// simple name of the controller class. its package will be
	private String className;

	// package of the class
	private String packageName;

	// fully qualified super class name of the controller class
	private String superClassName;

	// set of fully qualified names of the interfaces implemented by the controller
	private Set<String> interfaces = new HashSet<String>();

	public ClassControllerNode(String packageName, String className) {
		this.className = className;
		this.packageName = packageName;
	}

	public ClassControllerNode(String packageName, String className, String superClassName) {
		this.className = className;
		this.superClassName = superClassName;
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getSuperClassName() {
		return superClassName;
	}

	public Set<String> getInterfaces() {
		return interfaces;
	}

	public void addInterface(String interfaceName) {
		interfaces.add(interfaceName);
	}

	/*
	 * (non-Javadoc)
	 * @see jsfgenerator.generation.controller.nodes.ControllerNode#getRequiredImports()
	 */
	@Override
	public Set<String> getRequiredImports() {
		Set<String> imports = new HashSet<String>();
		
		if (superClassName != null) {
			imports.add(ControllerNodeUtils.getPackageName(superClassName));
		}
		
		return imports;
	}
}
