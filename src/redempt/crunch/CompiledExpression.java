package redempt.crunch;

import redempt.crunch.exceptions.ExpressionEvaluationException;
import redempt.crunch.token.Operation;
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

	public CompiledExpression(Value value) {
        setValue(value);
    }

    protected void setValue(Value value) {
        this.value = value;
        variableCount = recursiveVariableMaxIndex(value) + 1;
    }
	
	protected void setVariableValues(double[] values) {
		checkArgCount(values.length);
		variableValues = values;
	}

	private int recursiveVariableMaxIndex(Value value) {
		if (value.getType() == TokenType.VARIABLE) {
			Variable var = (Variable) value;
			var.expression = this;
			return var.getIndex();
		}
		if (value.getType() != TokenType.OPERATION) {
			return -1;
		}
		int count = -1;
		Operation operation = (Operation) value;
		for (Value val : operation.getValues()) {
			if (val.getType() == TokenType.VARIABLE) {
				Variable var = (Variable) val;
				var.expression = this;
				count = Math.max(count, var.getIndex());
			}
			if (val.getType() == TokenType.OPERATION) {
				count = Math.max(count, recursiveVariableMaxIndex(val));
			}
		}
		return count;
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
		return value.getValue();
	}
	
	/**
	 * Evaluates this CompiledExpression and returns its value without modifying variable values
	 * @return The resulting value
	 */
	public double evaluate() {
		checkArgCount(0);
		return value.getValue();
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
		return value.getValue();
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
		return value.getValue();
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
		return new CompiledExpression(value.getClone());
	}
	
	/**
	 * Converts this CompiledExpression back to a String which can be used to recreate it later
	 * @return A String representation of this CompiledExpression
	 */
	public String toString() {
		return value.toString();
	}
	
}
