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
	
	/**
	 * Create a Function
	 * @param name The function name
	 * @param argCount The number of arguments this Function will take
	 * @param func A lambda to take the arguments as a double array and return a value
	 */
	public Function(String name, int argCount, java.util.function.Function<double[], Double> func) {
		this.func = func;
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
		return func.apply(values);
	}
	
	@Override
	public TokenType getType() {
		return TokenType.FUNCTION;
	}
	
}
