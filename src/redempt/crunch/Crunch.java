package redempt.crunch;

import redempt.crunch.functional.EvaluationEnvironment;

/**
 * Public API methods for compiling expressions
 * @author Redempt
 */
public class Crunch {
	
	/**
	 * Compiles a mathematical expression into a CompiledExpression. Variables must be integers starting at 1 prefixed
	 * with $. Supported operations can be found in {@link Operator}, which lists the operations and their symbols
	 * for use in expressions. Parenthesis are also supported.
	 * @param expression The expression to compile
	 * @return The compiled expression
	 */
	public static CompiledExpression compileExpression(String expression) {
		return ExpressionCompiler.compile(expression, new EvaluationEnvironment());
	}
	
	/**
	 * Compiles a mathematical expression into a CompiledExpression. Variables must be integers starting at 1 prefixed
	 * with $. Supported operations can be found in {@link Operator}, which lists the operations and their symbols
	 * for use in expressions. Parenthesis are also supported.
	 * @param expression The expression to compile
	 * @param env The EvaluationEnvironment providing custom functions that can be used in the expression
	 * @return The compiled expression
	 */
	public static CompiledExpression compileExpression(String expression, EvaluationEnvironment env) {
		return ExpressionCompiler.compile(expression, env);
	}
	
	/**
	 * Compiles and evaluates an expression once. This is only for if you need a one-off evaluation of an expression
	 * which will not be evaluated again. If the expression will be evaluated multiple times, use {@link Crunch#compileExpression(String)}
	 * @param expression The expression to evaluate
	 * @param varValues The variable values for the expression
	 * @return The value of the expression
	 */
	public static double evaluateExpression(String expression, double... varValues) {
		CompiledExpression exp = ExpressionCompiler.compile(expression, new EvaluationEnvironment());
		return exp.evaluate(varValues);
	}
	
}
