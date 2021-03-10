package redempt.crunch.functional;

import redempt.crunch.Token;
import redempt.crunch.TokenType;
import redempt.crunch.Value;

/**
 * Represents a list of arguments being passed to a Function
 * @author Redempt
 */
public class ArgumentList implements Token {
	
	private Value[] arguments;
	
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
