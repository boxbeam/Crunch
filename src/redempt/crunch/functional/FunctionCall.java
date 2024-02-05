package redempt.crunch.functional;

import redempt.crunch.token.TokenType;
import redempt.crunch.token.Value;

/**
 * Represents a lazy function call with other lazy values as function arguments
 * @author Redempt
 */
public class FunctionCall implements Value {
	
	private final Value[] values;
	private final Function function;
	private final double[] numbers;
	
	public FunctionCall(Function function, Value[] values) {
		this.function = function;
		this.values = values;
		numbers = new double[function.getArgCount()];
	}
	
	@Override
	public TokenType getType() {
		return TokenType.FUNCTION_CALL;
	}
	
	@Override
	public double getValue(double[] variableValues) {
		for (int i = 0; i < values.length; i++) {
			numbers[i] = values[i].getValue(variableValues);
		}
		return function.call(numbers);
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
