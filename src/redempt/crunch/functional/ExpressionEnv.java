package redempt.crunch.functional;

import redempt.crunch.data.CharTree;
import redempt.crunch.token.*;
import redempt.crunch.Variable;

import java.util.Locale;
import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;

/**
 * Represents an environment containing functions that can be called in expressions
 * @author Redempt
 */
public class ExpressionEnv {
	
	private CharTree<Token> namedTokens;
	
	/**
	 * Creates a new EvaluationEnvironment
	 */
	public ExpressionEnv() {
		namedTokens = new CharTree<>();
		for (BinaryOperator op : BinaryOperator.values()) {
			namedTokens.set(op.symbol, op);
		}
		for (UnaryOperator op : UnaryOperator.values()) {
			namedTokens.set(op.symbol, op);
		}
		for (Constant con : Constant.values()) {
			namedTokens.set(con.toString().toLowerCase(Locale.ROOT), con);
		}
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
		namedTokens.set(function.getName(), function);
	}
	
	/**
	 * Adds any number of Functions that can be called from expressions with this environment
	 * @param functions The functions to add
	 */
	public void addFunctions(Function... functions) {
		for (Function function : functions) {
			addFunction(function);
		}
	}
	
	/**
	 * Adds a lazily-evaluated variable that will not need to be passed with the variable values
	 * @param name The name of the lazy variable
	 * @param supply A function to supply the value of the variable when needed
	 */
	public void addLazyVariable(String name, DoubleSupplier supply) {
		namedTokens.set(name, new LazyVariable(name, supply));
	}
	
	public void setVariableNames(String... names) {
		for (int i = 0; i < names.length; i++) {
			namedTokens.set(names[i], new Variable(null, i));
		}
	}
	
	/**
	 * Adds a Function that can be called from expressions with this environment
	 * @param name The function name
	 * @param argCount The argument count for the function
	 * @param func The lambda to accept the arguments as a double array and return a value
	 */
	public void addFunction(String name, int argCount, ToDoubleFunction<double[]> func) {
		addFunction(new Function(name, argCount, func));
	}
	
	/**
	 * Removes all functions
	 */
	public void clearFunctions() {
		namedTokens = new CharTree<>();
	}
	
	public CharTree<Token> getNamedTokens() {
		return namedTokens;
	}
	
}
