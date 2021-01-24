package redempt.crunch;

import redempt.crunch.exceptions.ExpressionEvaluationException;

public class CompiledExpression {
	
	protected double[] variableValues;
	private int variableCount;
	private Value value;
	
	protected CompiledExpression() {}
	
	protected void setVariableValues(double[] values) {
		if (values.length < variableCount) {
			throw new ExpressionEvaluationException("Too few variable values - expected " + variableCount + ", got " + values.length);
		}
		variableValues = values;
	}
	
	protected void setValue(Value value) {
		this.value = value;
		variableCount = recursiveVariableMaxIndex(value) + 1;
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
	
	protected Value getValue() {
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
	 * @return A clone of this CompiledExpression
	 */
	public CompiledExpression clone() {
		CompiledExpression clone = new CompiledExpression();
		Value cloned = value.clone();
		clone.setValue(cloned);
		return clone;
	}
	
	/**
	 * Converts this CompiledExpression back to a String which can be used to recreate it later
	 * @return A String representation of this CompiledExpression
	 */
	public String toString() {
		return value.toString();
	}
	
}
