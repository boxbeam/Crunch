package redempt.crunch;

class Operation implements Value {
	
	private Operator operator;
	private Value first;
	private Value second;
	
	protected Operation(Operator operator, Value value) {
		this.operator = operator;
		this.first = value;
	}
	
	protected Operation(Operator operator, Value first, Value second) {
		this.operator = operator;
		this.first = first;
		this.second = second;
	}
	
	public Value[] getValues() {
		return operator.isUnary() ?
				new Value[] {first} :
				new Value[] {first, second};
	}
	
	@Override
	public double getValue() {
		return operator.isUnary() ? operator.operate(first.getValue()) : operator.operate(first.getValue(), second.getValue());
	}
	
	@Override
	public TokenType getType() {
		return TokenType.OPERATION;
	}
	
	public String toString() {
		return "(" + (operator.isUnary() ?
				operator.getSymbol() + first.toString() :
				first.toString() + operator.getSymbol() + second.toString()) + ")";
	}
	
	public Operation getClone() {
		return operator.isUnary() ?
				new Operation(operator, first.getClone()) :
				new Operation(operator, first.getClone(), second.getClone());
	}
	
}
