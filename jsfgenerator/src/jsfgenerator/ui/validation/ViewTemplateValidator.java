package jsfgenerator.ui.validation;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jsfgenerator.generation.common.utilities.XMLParserUtils;
import jsfgenerator.generation.view.impl.ParserException;
import jsfgenerator.generation.view.impl.ViewTemplateConstants;
import jsfgenerator.ui.validation.ValidationRule.ValidationRuleType;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ViewTemplateValidator extends AbstractValidator {

	private static List<String> templateTreeNames = Arrays.asList(ViewTemplateConstants.ENTITY_PAGE,
			ViewTemplateConstants.ENTITY_FORM, ViewTemplateConstants.ENTITY_LIST_FORM, ViewTemplateConstants.ENTITY_LIST_PAGE,
			ViewTemplateConstants.LIST_COLLECTION_COLUMN, ViewTemplateConstants.LIST_COLLECTION_COLUMN_DATA,
			ViewTemplateConstants.LIST_COLUMN_DATA, ViewTemplateConstants.LIST_COLUMN_HEADER,
			ViewTemplateConstants.LIST_COLUMN_ACTION, ViewTemplateConstants.ACTION_BAR);

	private Document doc;
	private XMLParserUtils parserUtils;
	private IFile file;

	@Override
	public ValidationResult validate(IResource resource, int arg1, ValidationState state, IProgressMonitor monitor) {

		if (!(resource instanceof IFile)) {
			return null;
		}

		file = (IFile) resource;

		// check the full extension
		if (!file.getName().endsWith(".jsfgen.xml"))  {
			return null;
		}

		try {
			InputStream is = new FileInputStream(file.getLocation().toFile());
			doc = ValidatorDOMParser.createDocument(is);
		} catch (Exception e) {
			throw new RuntimeException("Validation error", e);
		}

		parserUtils = new XMLParserUtils(doc);

		List<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();
		for (ValidationRule rule : getValidationRules()) {
			List<ValidatorMessage> msgs = validate(rule);

			if (msgs != null) {
				messages.addAll(msgs);
			}
		}

		ValidationResult result = new ValidationResult();
		for (ValidatorMessage validatorMessage : messages) {
			result.add(validatorMessage);
		}

		return result;
	}

	private List<ValidationRule> getValidationRules() {
		List<ValidationRule> rules = new ArrayList<ValidationRule>();

		// root
		rules.add(new ValidationRule(ViewTemplateConstants.ROOT_XPATH, ViewTemplateConstants.ROOT_XPATH,
				"Entity list form description not found", ValidationRuleType.MANDATORY));

		// view template tree
		for (String treeName : templateTreeNames) {
			rules.add(createTemplateTreeValudationRule(treeName));
		}

		// entity page
		rules.addAll(createElementRules(ViewTemplateConstants.ENTITY_PAGE, Arrays.asList(ViewTemplateConstants.ENTITY_FORM,
				ViewTemplateConstants.ENTITY_LIST_FORM), null));

		// entity form
		rules.addAll(createElementRules(ViewTemplateConstants.ENTITY_FORM, Arrays.asList(ViewTemplateConstants.INPUT), Arrays
				.asList(ViewTemplateConstants.ACTION_BAR)));

		// TODO: entity list form

		// entity list page
		rules.addAll(createElementRules(ViewTemplateConstants.ENTITY_LIST_PAGE, Arrays.asList(
				ViewTemplateConstants.LIST_COLUMN_DATA, ViewTemplateConstants.LIST_COLUMN_HEADER), Arrays
				.asList(ViewTemplateConstants.ACTION_BAR)));

		rules.addAll(createVariableRules(ViewTemplateConstants.ENTITY_LIST_PAGE, ViewTemplateConstants.LIST_COLUMN_DATA));

		// column action
		rules.addAll(createElementRules(ViewTemplateConstants.LIST_COLUMN_ACTION, Arrays.asList(ViewTemplateConstants.ACTION),
				null));

		// collection column
		rules.addAll(createElementRules(ViewTemplateConstants.LIST_COLLECTION_COLUMN, Arrays
				.asList(ViewTemplateConstants.LIST_COLLECTION_COLUMN_DATA), null));

		rules.addAll(createVariableRules(ViewTemplateConstants.LIST_COLLECTION_COLUMN,
				ViewTemplateConstants.LIST_COLLECTION_COLUMN_DATA));

		// action bar
		rules.addAll(createElementRules(ViewTemplateConstants.ACTION_BAR, Arrays.asList(ViewTemplateConstants.ACTION), null));

		return rules;
	}

	private List<ValidationRule> createVariableRules(String root, String mandatoryElement) {
		List<ValidationRule> rules = new ArrayList<ValidationRule>();

		// var
		String exp = ViewTemplateConstants.getTemplateXPath(root) + ViewTemplateConstants.VARIABLE_XPATH;
		rules.add(new ValidationRule(exp, ViewTemplateConstants.getTemplateXPath(root), "Variable tag not found in " + root
				+ " tag tree", ValidationRuleType.MANDATORY));

		// mandatory elements
		exp = ViewTemplateConstants.getTemplateXPath(root) + ViewTemplateConstants.getPlaceholderXPath(mandatoryElement);
		rules.add(new ValidationRule(exp, ViewTemplateConstants.getTemplateXPath(root) + ViewTemplateConstants.VARIABLE_XPATH,
				"Variable tag does not contain mandatory element: " + mandatoryElement + " placeHolder",
				ValidationRuleType.MANDATORY));

		return rules;
	}

	private List<ValidationRule> createElementRules(String root, List<String> mandatoryElements, List<String> allowedElements) {
		List<ValidationRule> rules = new ArrayList<ValidationRule>();

		for (String element : mandatoryElements) {
			String exp = ViewTemplateConstants.getTemplateXPath(root) + ViewTemplateConstants.getPlaceholderXPath(element);
			rules.add(new ValidationRule(exp, ViewTemplateConstants.getTemplateXPath(root), "Placeholder tag for " + element
					+ " tree is not defined in " + root + " tree", ValidationRuleType.MANDATORY));
		}

		for (String treeName : templateTreeNames) {
			if (!mandatoryElements.contains(treeName) && (allowedElements == null || !allowedElements.contains(treeName))) {
				String exp = ViewTemplateConstants.getTemplateXPath(root) + ViewTemplateConstants.getPlaceholderXPath(treeName);
				rules.add(new ValidationRule(exp, ViewTemplateConstants.getTemplateXPath(root), "Placeholder tag for " + treeName
						+ " tree is not allowed in " + root + " tree", ValidationRuleType.FORBIDDEN));
			}
		}

		return rules;
	}

	private ValidationRule createTemplateTreeValudationRule(String treeName) {
		String exp = ViewTemplateConstants.getTemplateXPath(treeName);
		return new ValidationRule(exp, exp, treeName + " description not found", ValidationRuleType.MANDATORY);
	}

	private List<ValidatorMessage> validate(ValidationRule rule) {

		NodeList nodeList = null;
		try {
			nodeList = parserUtils.getNodes(rule.getValidateFor());
		} catch (ParserException e) {
			ValidatorMessage message = ValidatorMessage.create("Could not parse the xml file.", file);
			message.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			message.setAttribute(IMarker.LINE_NUMBER, 1);

			return Arrays.asList(message);
		}

		if (ValidationRuleType.MANDATORY.equals(rule.getType()) && (nodeList == null || nodeList.getLength() == 0)) {
			ValidatorMessage message = ValidatorMessage.create(rule.getErrorMessage(), file);

			int lineNumber = 1;
			if (!rule.getValidateFor().equals(rule.getTargetNode())) {
				Node targetNode;
				try {
					targetNode = parserUtils.getNode(rule.getTargetNode());
					if (targetNode != null) {
						lineNumber = Integer.valueOf((String) targetNode.getUserData(ValidatorDOMParser.LINE_NUMBER_KEY));
					}
				} catch (ParserException e) {
					ValidatorMessage msg = ValidatorMessage.create("Could not parse the xml file.", file);
					msg.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
					msg.setAttribute(IMarker.LINE_NUMBER, 1);

					return Arrays.asList(msg);
				}
			}

			message.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			message.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			return Arrays.asList(message);
		} else if (ValidationRuleType.FORBIDDEN.equals(rule.getType()) && (nodeList == null || nodeList.getLength() != 0)) {
			List<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				ValidatorMessage message = ValidatorMessage.create(rule.getErrorMessage(), file);
				message.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				int lineNumber = Integer.valueOf((String) nodeList.item(i).getUserData(ValidatorDOMParser.LINE_NUMBER_KEY));
				message.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				messages.add(message);
			}

			return messages;
		}

		return null;
	}

}
