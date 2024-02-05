package redempt.crunch;

import redempt.crunch.token.TokenType;
import redempt.crunch.token.Value;

public class Variable implements Value {
	
	private final int index;
	
	public Variable(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	@Override
	public double getValue(double[] variableValues) {
		return variableValues[index];
	}
	
	@Override
	public TokenType getType() {
		return TokenType.VARIABLE;
	}
	
	public String toString() {
		return "$" + (index + 1);
	}
	
	public Variable getClone() {
		return new Variable(index);
	}
	
}
