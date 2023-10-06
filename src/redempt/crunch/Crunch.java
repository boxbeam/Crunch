package redempt.crunch;

import redempt.crunch.functional.ExpressionEnv;
import redempt.crunch.token.BinaryOperator;

/**
 * Public API methods for compiling expressions
 * @author Redempt
 */
public class Crunch {
	
	private static final ExpressionEnv DEFAULT_EVALUATION_ENVIRONMENT = new ExpressionEnv();
	
	/**
	 * Compiles a mathematical expression into a CompiledExpression. Variables must be integers starting at 1 prefixed
	 * with $. Supported operations can be found in {@link BinaryOperator}, which lists the operations and their symbols
	 * for use in expressions. Parenthesis are also supported.
	 * @param expression The expression to compile
	 * @return The compiled expression
	 */
	public static CompiledExpression compileExpression(String expression) {
		return new ExpressionParser(expression, DEFAULT_EVALUATION_ENVIRONMENT).parse();
	}
	
	/**
	 * Compiles a mathematical expression into a CompiledExpression. Variables must be integers starting at 1 prefixed
	 * with $. Supported operations can be found in {@link BinaryOperator}, which lists the operations and their symbols
	 * for use in expressions. Parenthesis are also supported.
	 * @param expression The expression to compile
	 * @param env The EvaluationEnvironment providing custom functions that can be used in the expression
	 * @return The compiled expression
	 */
	public static CompiledExpression compileExpression(String expression, ExpressionEnv env) {
		return new ExpressionParser(expression, env).parse();
	}
	
	/**
	 * Compiles and evaluates an expression once. This is only for if you need a one-off evaluation of an expression
	 * which will not be evaluated again. If the expression will be evaluated multiple times, use {@link Crunch#compileExpression(String)}
	 * @param expression The expression to evaluate
	 * @param varValues The variable values for the expression
	 * @return The value of the expression
	 */
	public static double evaluateExpression(String expression, double... varValues) {
		CompiledExpression exp = new ExpressionParser(expression, DEFAULT_EVALUATION_ENVIRONMENT).parse();
		return exp.evaluate(varValues);
	}
	
}
