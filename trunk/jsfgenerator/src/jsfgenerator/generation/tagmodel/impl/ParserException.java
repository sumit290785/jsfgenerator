package jsfgenerator.generation.tagmodel.impl;

/**
 * Tag tree parser exception
 * 
 * @author zoltan verebes
 * 
 */
public class ParserException extends Exception {

	private static final long serialVersionUID = 6510447197990576090L;

	public ParserException(String msg) {
		super(msg);
	}

	public ParserException(String msg, Exception e) {
		super(msg, e);
	}

}
