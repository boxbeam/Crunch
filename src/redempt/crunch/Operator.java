package redempt.crunch;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents an Operator which can be used in mathematical expressions
 * @author Redempt
 */
public enum Operator implements Token {
	
	RANDOM_DOUBLE("rand", 6, d -> CompiledExpression.random.nextDouble() * d),
	ROUND("round", 6, d -> (double) Math.round(d)),
	CEILING("ceil", 6, d -> Math.ceil(d)),
	FLOOR("floor", 6, d -> Math.floor(d)),
	ARC_SINE("asin", 6, d -> Math.asin(d)),
	ARC_COSINE("acos", 6, d -> Math.acos(d)),
	ARC_TANGENT("atan", 6, d -> Math.acos(d)),
	SINE("sin", 6, d -> Math.sin(d)),
	COSINE("cos", 6, d -> Math.cos(d)),
	TANGENT("tan", 6, d -> Math.tan(d)),
	ABSOLUTE_VALUE("abs", 6, d -> Math.abs(d)),
	LOGARITHM("log", 6, d -> Math.log(d)),
	SQUARE_ROOT("sqrt", 6, d -> Math.sqrt(d)),
	EXPONENT("^", 5, (a, b) -> Math.pow(a, b)),
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
