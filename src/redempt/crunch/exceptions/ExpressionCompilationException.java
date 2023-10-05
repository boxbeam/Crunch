package redempt.crunch.exceptions;

import redempt.crunch.Parser;

public class ExpressionCompilationException extends RuntimeException {
	
	private Parser parser;

	public ExpressionCompilationException(Parser parser, String message) {
		super(message);
		this.parser = parser;
	}

	public Parser getParser() {
		return parser;
	}
	
}
