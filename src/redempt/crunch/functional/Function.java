package redempt.crunch.functional;

import redempt.crunch.token.Token;
import redempt.crunch.token.TokenType;

import java.util.function.ToDoubleFunction;

/**
 * Represents a function which can be called in expressions whose environments have it
 * @author Redempt
 */
public class Function implements Token {
	
	private final String name;
	private final int argCount;
	private final ToDoubleFunction<double[]> function;
	
	/**
	 * Create a Function
	 * @param name The function name
	 * @param argCount The number of arguments this Function will take
	 * @param function A lambda to take the arguments as a double array and return a value
	 */
	public Function(String name, int argCount, ToDoubleFunction<double[]> function) {
		this.function = function;
		this.name = name;
		this.argCount = argCount;
	}
	
	/**
	 * @return The name of this function
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return The number of arguments this function takes
	 */
	public int getArgCount() {
		return argCount;
	}
	
	/**
	 * Calls this function with a set of values - Warning, no validation is done on array size
	 * @param values The input values
	 * @return The output value
	 */
	public double call(double[] values) {
		return function.applyAsDouble(values);
	}
	
	@Override
	public TokenType getType() {
		return TokenType.FUNCTION;
	}
	
}
