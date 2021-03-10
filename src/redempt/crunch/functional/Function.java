package redempt.crunch.functional;

import redempt.crunch.Token;
import redempt.crunch.TokenType;

/**
 * Represents a function which can be called in expressions whose environments have it
 * @author Redempt
 */
public class Function implements Token {
	
	private String name;
	private int argCount;
	private java.util.function.Function<double[], Double> func;
	
	public Function(String name, int argCount, java.util.function.Function<double[], Double> func) {
		this.func = func;
		this.name = name;
		this.argCount = argCount;
	}
	
	public String getName() {
		return name;
	}
	
	public int getArgCount() {
		return argCount;
	}
	
	public double call(double[] values) {
		return func.apply(values);
	}
	
	@Override
	public TokenType getType() {
		return TokenType.FUNCTION;
	}
	
}
