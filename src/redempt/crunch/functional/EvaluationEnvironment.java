package redempt.crunch.functional;

import redempt.crunch.CharTree;
import redempt.crunch.token.Constant;
import redempt.crunch.token.Operator;
import redempt.crunch.token.Token;
import redempt.crunch.Variable;

import java.util.Locale;

/**
 * Represents an environment containing functions that can be called in expressions
 * @author Redempt
 */
public class EvaluationEnvironment {
	
	private CharTree<Token> namedTokens;
	
	/**
	 * Creates a new EvaluationEnvironment
	 */
	public EvaluationEnvironment() {
		namedTokens = new CharTree<>();
		for (Operator op : Operator.values()) {
			if (op.isInternal()) {
				continue;
			}
			namedTokens.set(op.getSymbol(), op);
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
	
	public void setVariableNames(String... names) {
		for (int i = 0; i < names.length; i++) {
			namedTokens.set(names[i], new Variable(null, i + 1));
		}
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
