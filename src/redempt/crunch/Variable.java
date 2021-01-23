package redempt.crunch;

public class Variable implements Value {
	
	private int index;
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
	
	public Variable clone() {
		return new Variable(expression, index);
	}
	
}
