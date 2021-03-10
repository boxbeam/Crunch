package redempt.crunch.functional;

import redempt.crunch.TokenType;
import redempt.crunch.Value;

/**
 * Represents a lazy function call with other lazy values as function arguments
 * @author Redempt
 */
public class FunctionCall implements Value {
	
	private Value[] values;
	private Function function;
	
	public FunctionCall(Function function, Value[] values) {
		this.function = function;
		this.values = values;
	}
	
	@Override
	public TokenType getType() {
		return TokenType.FUNCTION_CALL;
	}
	
	@Override
	public double getValue() {
		double[] args = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			args[i] = values[i].getValue();
		}
		return function.call(args);
	}
	
	@Override
	public Value getClone() {
		Value[] clone = new Value[values.length];
		System.arraycopy(values, 0, clone, 0, values.length);
		return new FunctionCall(function, values);
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder(function.getName()).append('(');
		for (int i = 0; i < values.length; i++) {
			Value arg = values[i];
			builder.append(arg.toString());
			if (i != values.length - 1) {
				builder.append(", ");
			}
		}
		return builder.append(')').toString();
	}
	
}
