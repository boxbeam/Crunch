package redempt.crunch.token;

import java.util.function.DoubleSupplier;

public class LazyVariable implements Value {
	
	private final String name;
	private final DoubleSupplier supplier;
	
	public LazyVariable(String name, DoubleSupplier supplier) {
		this.name = name;
		this.supplier = supplier;
	}
	
	@Override
	public TokenType getType() {
		return TokenType.LAZY_VARIABLE;
	}
	
	@Override
	public double getValue(double[] variableValues) {
		return supplier.getAsDouble();
	}
	
	@Override
	public Value getClone() {
		return this;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
