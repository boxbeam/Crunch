package redempt.crunch;

import redempt.crunch.exceptions.ExpressionEvaluationException;
import redempt.crunch.token.BinaryOperation;
import redempt.crunch.token.TokenType;
import redempt.crunch.token.Value;

/**
 * An expression which has been compiled with {@link Crunch#compileExpression(String)} and can be evaluated with {@link CompiledExpression#evaluate(double...)}
 * @author Redempt
 */
public class CompiledExpression {
	
	protected double[] variableValues;
	private int variableCount;
	private Value value;
	
    protected CompiledExpression() {}

	public CompiledExpression(Value value, int variableCount) {
        initialize(value, variableCount);
    }

    protected void initialize(Value value, int variableCount) {
        this.value = value;
		this.variableCount = variableCount;
    }
	
	protected void setVariableValues(double[] values) {
		checkArgCount(values.length);
		variableValues = values;
	}
	
    /**
     * Gets the internal Value representation of the expression. This is essentially reflection into the expression. Proceed at your own risk.
     * @return The Value this CompiledExpression wraps
     */
	public Value getValue() {
		return value;
	}
	
	/**
	 * Gets the highest index of variables used in this expression. Any call to {@link CompiledExpression#evaluate(double...)}
	 * must pass at least this many values.
	 * @return The number of variables used in this expression
	 */
	public int getVariableCount() {
		return variableCount;
	}
	
	/**
	 * Evaluates this CompiledExpression and returns its value
	 * @param values The values for variables used in this expression, in order starting with 1
	 * @return The resulting value
	 */
	public double evaluate(double... values) {
		setVariableValues(values);
		return value.getValue(this.variableValues);
	}
	
	/**
	 * Evaluates this CompiledExpression and returns its value without modifying variable values
	 * @return The resulting value
	 */
	public double evaluate() {
		checkArgCount(0);
		return value.getValue(this.variableValues);
	}
	
	/**
	 * Evaluates this CompiledExpression and returns its value
	 * @param first The first variable value
	 * @return The resulting value
	 */
	public double evaluate(double first) {
		checkArgCount(1);
		if (variableValues == null) {
			variableValues = new double[1];
		}
		variableValues[0] = first;
		return value.getValue(this.variableValues);
	}
	
	/**
	 * Evaluates this CompiledExpression and returns its value
	 * @param first The first variable value
	 * @param second The second variable value
	 * @return The resulting value
	 */
	public double evaluate(double first, double second) {
		checkArgCount(2);
		if (variableValues == null) {
			variableValues = new double[2];
		}
		variableValues[0] = first;
		variableValues[1] = second;
		return value.getValue(this.variableValues);
	}
	
	private void checkArgCount(int args) {
		if (variableCount > args) {
			throw new ExpressionEvaluationException("Too few variable values - expected " + variableCount + ", got " + args);
		}
	}
	
	/**
	 * @return A clone of this CompiledExpression
	 */
	public CompiledExpression clone() {
		return new CompiledExpression(value.getClone(), variableCount);
	}
	
	/**
	 * Converts this CompiledExpression back to a String which can be used to recreate it later
	 * @return A String representation of this CompiledExpression
	 */
	public String toString() {
		return value.toString();
	}
	
}
