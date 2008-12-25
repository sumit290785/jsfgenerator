package jsfgenerator.generation.controller.nodes;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jsfgenerator.generation.controller.FunctionType;
import jsfgenerator.generation.controller.blockimplementation.InitStatementWrapper;

/**
 * 
 * @author zoltan verebes
 * 
 */
public class FunctionControllerNode extends ControllerNode {

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
	 * @see jsfgenerator.generation.controller.nodes.ControllerNode#getRequiredImports ()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getRequiredImports() {
		Set<String> imports = super.getRequiredImports();
		
		for (Object obj : arguments) {
			if (obj instanceof Collection) {
				for (Object element : (Collection)obj) {
					if (element instanceof InitStatementWrapper) {
						imports.add(((InitStatementWrapper)element).getEntityClass());
					}
				}
			}
		}
		
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
