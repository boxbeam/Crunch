package redempt.crunch;

class LiteralValue implements Value {
	
	private double value;
	
	public LiteralValue(double value) {
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
	
	public String toString() {
		return value + "";
	}
	
	public LiteralValue getClone() {
		return new LiteralValue(value);
	}
	
}
