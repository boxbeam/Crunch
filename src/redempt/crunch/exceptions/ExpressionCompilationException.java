package redempt.crunch.exceptions;

import redempt.crunch.ExpressionParser;

public class ExpressionCompilationException extends RuntimeException {
	
	private ExpressionParser parser;

	public ExpressionCompilationException(ExpressionParser parser, String message) {
		super(message);
		this.parser = parser;
	}

	public ExpressionParser getParser() {
		return parser;
	}
	
}
