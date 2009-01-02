package jsfgenerator.generation.common.visitors;

public class EvaluationException extends Exception {

	private static final long serialVersionUID = -3257118212828599692L;

	public EvaluationException(String msg, Exception e) {
		super(msg, e);
	}

	public EvaluationException(Exception e) {
		super(e);
	}

	public EvaluationException(String msg) {
		super(msg);
	}
}
