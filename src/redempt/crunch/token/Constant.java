package redempt.crunch.token;

import java.util.Locale;

/**
 * Represents a mathematical or boolean constant which can be used in an expression
 * @author Redempt
 */
public enum Constant implements Value {
	
	PI(Math.PI),
	E(Math.E),
	TRUE(1),
	FALSE(0);
	
	private final double value;
	
	Constant(double value) {
		this.value = value;
	}
	
	@Override
	public TokenType getType() {
		return TokenType.LITERAL_VALUE;
	}
	
	@Override
	public double getValue() {
		return value;
	}
	
	@Override
	public Value getClone() {
		return this;
	}
	
	@Override
	public String toString() {
		return super.toString().toLowerCase(Locale.ROOT);
	}
}
