package jsfgenerator.generation.common;

public class GenerationException extends RuntimeException {

	private static final long serialVersionUID = -147050971539179737L;

	public GenerationException() {
		
	}
	
	public GenerationException(String msg, Exception e) {
		super(msg, e);
	}
	
	public GenerationException(String msg) {
		super(msg);
	}
}
