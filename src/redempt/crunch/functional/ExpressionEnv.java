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

	private CharTree<BinaryOperator> binaryOperators = new CharTree<>();
	private CharTree<Token> leadingOperators = new CharTree<>();
	private CharTree<Value> values = new CharTree<>();

	private int varCount = 0;
	
	/**
	 * Creates a new EvaluationEnvironment
	 */
	public ExpressionEnv() {
		for (BinaryOperator op : BinaryOperator.values()) {
			binaryOperators.set(op.symbol, op);
		}
		for (UnaryOperator op : UnaryOperator.values()) {
			leadingOperators.set(op.symbol, op);
		}
		for (Constant con : Constant.values()) {
			values.set(con.toString().toLowerCase(Locale.ROOT), con);
		}
	}

	private void checkName(String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Identifier cannot be empty or null");
		}
		if (!Character.isAlphabetic(name.charAt(0))) {
			throw new IllegalArgumentException("Identifier must begin with an alphabetic character");
		}
	}

	/**
	 * Adds a Function that can be called from expressions with this environment
	 * @param function The function
	 */
	public void addFunction(Function function) {
		checkName(function.getName());
		char[] chars = function.getName().toCharArray();
		leadingOperators.set(function.getName(), function);
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
		checkName(name);
		values.set(name, new LazyVariable(name, supply));
	}
	
	public void setVariableNames(String... names) {
		varCount = names.length;
		for (int i = 0; i < names.length; i++) {
			checkName(names[i]);
			values.set(names[i], new Variable(null, i));
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
	 * @return The prefix tree of all leading operators, including unary operators and functions
	 */
	public CharTree<Token> getLeadingOperators() {
		return leadingOperators;
	}

	/**
	 * @return The prefix tree of all binary operators
	 */
	public CharTree<BinaryOperator> getBinaryOperators() {
		return binaryOperators;
	}

	/**
	 * @return The prefix tree of all values, including constants, variables, and lazy variables
	 */
	public CharTree<Value> getValues() {
		return values;
	}

	/**
	 * @return The number of variables in this expression environment
	 */
	public int getVariableCount() {
		return varCount;
	}
	
}
