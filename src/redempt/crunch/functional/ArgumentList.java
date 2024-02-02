package redempt.crunch.functional;

import redempt.crunch.token.Token;
import redempt.crunch.token.TokenType;
import redempt.crunch.token.Value;

/**
 * Represents a list of arguments being passed to a Function
 * @author Redempt
 */
public class ArgumentList implements Token {
	
	private final Value[] arguments;
	
	public ArgumentList(Value[] arguments) {
		this.arguments = arguments;
	}
	
	public Value[] getArguments() {
		return arguments;
	}
	
	@Override
	public TokenType getType() {
		return TokenType.ARGUMENT_LIST;
	}
	
}
