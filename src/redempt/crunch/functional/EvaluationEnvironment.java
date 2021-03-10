package redempt.crunch.functional;

import redempt.crunch.CharTree;

/**
 * Represents an environment containing functions that can be called in expressions
 * @author Redempt
 */
public class EvaluationEnvironment {
	
	private CharTree<Function> functions;
	
	/**
	 * Creates a new EvaluationEnvironment
	 */
	public EvaluationEnvironment() {
		functions = new CharTree<>();
	}
	
	/**
	 * Adds a Function that can be called from expressions with this environment
	 * @param function The function
	 */
	public void addFunction(Function function) {
		char[] chars = function.getName().toCharArray();
		for (char c : chars) {
			if (c > 255) {
				throw new IllegalArgumentException("Function names must be ASCII only");
			}
		}
		functions.set(function.getName(), function);
	}
	
	/**
	 * Adds a Function that can be called from expressions with this environment
	 * @param name The function name
	 * @param argCount The argument count for the function
	 * @param func The lambda to accept the arguments as a double array and return a value
	 */
	public void addFunction(String name, int argCount, java.util.function.Function<double[], Double> func) {
		addFunction(new Function(name, argCount, func));
	}
	
	public void clearFunctions() {
		functions = new CharTree<>();
	}
	
	public CharTree<Function> getFunctions() {
		return functions;
	}
	
}
