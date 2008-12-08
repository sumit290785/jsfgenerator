package jsfgenerator.generation.controller.nodes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author zoltan verebes
 * 
 */
public class FunctionControllerNode extends ControllerNode {

	public enum FunctionType {
		GETTER, SETTER, SAVE, UPDATE, DELETE, EMPTY, ADD, REMOVE
	}

	private String functionName;

	private String returnType;

	private Map<String, String> parameters = new HashMap<String, String>();

	private FunctionType type;

	private Object[] arguments;

	public FunctionControllerNode(String functionName, FunctionType type, Object... arguments) {
		this(functionName, null, type, arguments);
	}

	public FunctionControllerNode(String functionName, String returnType, FunctionType type, Object... arguments) {
		this.functionName = functionName;
		this.returnType = returnType;
		this.type = type;
		this.arguments = arguments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jsfgenerator.generation.controller.nodes.ControllerNode#getRequiredImports
	 * ()
	 */
	@Override
	public Set<String> getRequiredImports() {
		Set<String> imports = new HashSet<String>();
		return imports;
	}

	public String getFunctionName() {
		return functionName;
	}

	public String getReturnType() {
		return returnType;
	}

	public void addParameter(String name, String type) {
		parameters.put(name, type);
	}

	public List<String> getParameterNames() {
		return Arrays.asList(parameters.keySet().toArray(new String[0]));

	}

	public String getType(String name) {
		return parameters.get(name);
	}

	public FunctionType getType() {
		return type;
	}

	public Object[] getArguments() {
		return arguments;
	}
}
