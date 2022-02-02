package redempt.crunch.token;

import java.util.function.DoubleSupplier;

public class LazyVariable implements Value {
	
	private String name;
	private DoubleSupplier supplier;
	
	public LazyVariable(String name, DoubleSupplier supplier) {
		this.name = name;
		this.supplier = supplier;
	}
	
	@Override
	public TokenType getType() {
		return TokenType.LITERAL_VALUE;
	}
	
	@Override
	public double getValue() {
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
