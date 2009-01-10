package jsfgenerator.ui.validation;

/**
 * Validation rules are evaluated for annotation nodes in the view descriptor file. It has a target node defined with an XPath expression,
 * the type of the rule which is mandatory or forbidden, a target of the error message, which is a node defined with an XPath (error message
 * is populated in the row of the target node) and the error message in case of an error.
 * 
 * It is a data holder class used during validation
 * 
 * @author zoltan verebes
 * 
 */
public class ValidationRule {

	public static enum ValidationRuleType {
		MANDATORY, FORBIDDEN
	}

	// XPath expression
	private String validateFor;

	// XPath expression for error message
	private String targetNode;

	// Message in case of the error shows up
	private String errorMessage;

	private ValidationRuleType type;

	public ValidationRule(String validateFor, String targetNode, String errorMessage, ValidationRuleType type) {
		this.validateFor = validateFor;
		this.targetNode = targetNode;
		this.errorMessage = errorMessage;
		this.type = type;
	}

	public String getValidateFor() {
		return validateFor;
	}

	public String getTargetNode() {
		return targetNode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public ValidationRuleType getType() {
		return type;
	}

}
