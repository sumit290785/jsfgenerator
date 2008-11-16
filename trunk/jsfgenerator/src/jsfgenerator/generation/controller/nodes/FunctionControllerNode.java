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

	private String functionName;

	private String returnType;

	private Map<String, String> parameters = new HashMap<String, String>();

	public FunctionControllerNode(String functionName) {
		this.functionName = functionName;
		this.returnType = null;
	}

	public FunctionControllerNode(String functionName, String returnType) {
		this.functionName = functionName;
		this.returnType = returnType;
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

	public List<String> getParameters() {
		return Arrays.asList(parameters.values().toArray(new String[0]));

	}

	public String getType(String name) {
		return parameters.get(name);
	}

}
