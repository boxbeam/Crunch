package redempt.crunch;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents an Operator which can be used in mathematical expressions
 * @author Redempt
 */
public enum Operator implements Token {
	
	SINE("sin", 6, Math::sin),
	COSINE("cos", 6, Math::cos),
	TANGENT("tan", 6, Math::tan),
	ABSOLUTE_VALUE("abs", 6, Math::abs),
	SQUARE_ROOT("sqrt", 6, Math::sqrt),
	EXPONENT("^", 5, Math::pow),
	MULTIPLY("*", 4, (a, b) -> a * b),
	DIVIDE("/", 4, (a, b) -> a / b),
	MODULUS("%", 4, (a, b) -> a % b),
	ADD("+", 3, (a, b) -> a + b),
	SUBTRACT("-", 3, (a, b) -> a - b);
	
	private String name;
	private boolean unary;
	private BiFunction<Double, Double, Double> operate;
	private int priority;
	
	private Operator(String name, int priority, BiFunction<Double, Double, Double> operate) {
		this.name = name;
		this.operate = operate;
		this.unary = false;
		this.priority = priority;
	}
	
	private Operator(String name, int priority, Function<Double, Double> operate) {
		this.name = name;
		this.operate = (a, b) -> operate.apply(a);
		this.unary = true;
		this.priority = priority;
	}
	
	/**
	 * @return The priority of the operation in evaluation - higher priority will be evaluated first
	 */
	public int getPriority() {
		return priority;
	}
	
	/**
	 * The symbol to represent this Operator in expressions
	 * @return
	 */
	public String getSymbol() {
		return name;
	}
	
	/**
	 * @return Whether this Operator operates on only one value
	 */
	public boolean isUnary() {
		return unary;
	}
	
	/**
	 * Applies this Operator to the given values
	 * @param first The first value
	 * @param second The second value
	 * @return The resulting value
	 */
	public double operate(double first, double second) {
		return operate.apply(first, second);
	}
	
	/**
	 * Applies this Operator to one value
	 * @param value The operand value
	 * @return The resulting value
	 */
	public double operate(double value) {
		return operate.apply(value, 0d);
	}
	
	@Override
	public TokenType getType() {
		return TokenType.OPERATOR;
	}
	
	public String toString() {
		return getSymbol();
	}
	
}
