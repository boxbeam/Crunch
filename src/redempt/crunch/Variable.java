package redempt.crunch;

import redempt.crunch.token.TokenType;
import redempt.crunch.token.Value;

public class Variable implements Value {
	
	private final int index;
	protected CompiledExpression expression;
	
	public Variable(CompiledExpression expression, int index) {
		this.expression = expression;
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	@Override
	public double getValue() {
		return expression.variableValues[index];
	}
	
	@Override
	public TokenType getType() {
		return TokenType.VARIABLE;
	}
	
	public String toString() {
		return "$" + (index + 1);
	}
	
	public Variable getClone() {
		return new Variable(expression, index);
	}
	
}
